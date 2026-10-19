"""Generate the two original DOOM layers: a low attraction rumble and a heavy impact.

Both are synthesized from scratch by this repository's own code (sine partials, filtered
noise and envelopes). They do not sample the third-party tripod film recording, which stays
in place at the user's request. Run with `python scripts/generate_doom_audio.py`.
"""
from array import array
from pathlib import Path
import hashlib
import math
import random
import shutil
import subprocess
import tempfile
import wave

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "MahouTsukai-Fabric/src/main/resources/assets/mahoutsukai"
RATE = 32000


def lowpass(samples, alpha):
    out = array("f")
    value = 0.0
    for sample in samples:
        value += alpha * (sample - value)
        out.append(value)
    return out


def normalise(samples, peak):
    loudest = max(1e-9, max(abs(s) for s in samples))
    scale = peak / loudest
    return array("f", (s * scale for s in samples))


def rumble(seconds=4.0):
    """Slow, oppressive sub-bass with a breathing envelope and a distant grinding texture."""
    random.seed(20261019)
    count = int(seconds * RATE)
    noise = lowpass([random.uniform(-1.0, 1.0) for _ in range(count)], 0.0016)
    out = []
    for i in range(count):
        t = i / RATE
        swell = 0.35 + 0.65 * (0.5 + 0.5 * math.sin(t * 2 * math.pi * 0.17 - math.pi / 2))
        grind = 0.5 + 0.5 * math.sin(t * 2 * math.pi * 0.63)
        sub = (
            math.sin(2 * math.pi * 36.0 * t)
            + 0.7 * math.sin(2 * math.pi * 48.0 * t + 1.1)
            + 0.35 * math.sin(2 * math.pi * 24.0 * t + 0.4)
        )
        texture = noise[i] * (0.55 + 0.45 * grind)
        out.append(0.5 * sub * swell + 0.9 * texture)
    fade = int(0.5 * RATE)
    tail = int(1.2 * RATE)
    for i in range(min(fade, count)):
        out[i] *= i / fade
    for i in range(min(tail, count)):
        out[count - 1 - i] *= i / tail
    return normalise(array("f", out), 0.72)


def impact(seconds=2.9):
    """Pitch-dropping sub hit, a filtered noise burst, then a metallic decaying ring."""
    random.seed(20261020)
    count = int(seconds * RATE)
    noise = lowpass([random.uniform(-1.0, 1.0) for _ in range(count)], 0.05)
    out = []
    phase = 0.0
    for i in range(count):
        t = i / RATE
        drop = 118.0 * math.exp(-t * 2.4) + 29.0
        phase += 2 * math.pi * drop / RATE
        body = math.sin(phase) * math.exp(-t * 2.1)
        burst = noise[i] * math.exp(-t * 7.0) * 1.6
        ring = (
            math.sin(2 * math.pi * 214.0 * t)
            + 0.6 * math.sin(2 * math.pi * 327.0 * t + 0.7)
        ) * math.exp(-t * 3.2) * 0.18
        out.append(0.85 * body + burst + ring)
    tail = int(1.4 * RATE)
    for i in range(min(tail, count)):
        out[count - 1 - i] *= i / tail
    return normalise(array("f", out), 0.92)


def staff_blast(seconds=1.9):
    """Bright crack plus a short sub drop for the staff's big explosion mode."""
    random.seed(20261021)
    count = int(seconds * RATE)
    noise = lowpass([random.uniform(-1.0, 1.0) for _ in range(count)], 0.35)
    out = []
    phase = 0.0
    for i in range(count):
        t = i / RATE
        drop = 96.0 * math.exp(-t * 3.1) + 41.0
        phase += 2 * math.pi * drop / RATE
        sub = math.sin(phase) * math.exp(-t * 2.6)
        crack = noise[i] * math.exp(-t * 9.0) * 2.0
        spark = math.sin(2 * math.pi * 640.0 * t) * math.exp(-t * 6.0) * 0.12
        out.append(0.9 * sub + crack + spark)
    tail = int(0.8 * RATE)
    for i in range(min(tail, count)):
        out[count - 1 - i] *= i / tail
    return normalise(array("f", out), 0.95)


def write_to(destination, name, samples):
    destination.mkdir(parents=True, exist_ok=True)
    wav = Path(tempfile.mkstemp(suffix=".wav")[1])
    try:
        pcm = array("h")
        for sample in samples:
            value = int(max(-1.0, min(1.0, sample)) * 32767)
            pcm.append(value)
        with wave.open(str(wav), "wb") as writer:
            writer.setparams((1, 2, RATE, 0, "NONE", "not compressed"))
            writer.writeframes(pcm.tobytes())
        output = destination / (name + ".ogg")
        subprocess.run(
            [shutil.which("ffmpeg"), "-y", "-v", "error", "-i", str(wav),
             "-map_metadata", "-1", "-ac", "1", "-ar", str(RATE), "-c:a", "libvorbis",
             "-q:a", "4", str(output)],
            check=True,
        )
        digest = hashlib.sha256(output.read_bytes()).hexdigest()
        print(f"{output.relative_to(ROOT)} {output.stat().st_size} bytes sha256={digest}")
    finally:
        try:
            wav.unlink(missing_ok=True)
        except PermissionError:
            # Windows may keep the temporary encode input locked briefly; it is only a temp file.
            pass


def write(name, samples):
    write_to(ASSETS / "sounds/doom", name, samples)


def main():
    if not shutil.which("ffmpeg"):
        raise SystemExit("ffmpeg is required to encode the generated sounds")
    write("rumble", rumble())
    write("impact", impact())
    write_to(ASSETS / "sounds/mystic", "big_explosion", staff_blast())


if __name__ == "__main__":
    main()
