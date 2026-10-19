"""Original Archmage bell/energy cues, synthesized without samples (Python + ffmpeg).

Run: python scripts/generate_archmage_audio.py
Generate just the entrance: python scripts/generate_archmage_audio.py --only arrival
Generate just the laugh: python scripts/generate_archmage_audio.py --only defeat_laugh
The encoder output is decoded again to verify mono, rate, duration and peak.
"""
import argparse
from array import array
import hashlib
import json
import math
from pathlib import Path
import random
import shutil
import subprocess
import tempfile
import wave

ROOT = Path(__file__).resolve().parents[1]
DEST = ROOT / "MahouTsukai-Fabric/src/main/resources/assets/mahoutsukai/sounds/archmage"
RATE = 32000
TAU = math.tau
CUES = (("charge", .9, .72), ("release", .45, .78), ("shockwave", 1.3, .82),
        ("arrival", 2.5, .78), ("defeat_laugh", 3.2, .78))


def synth(name, seconds, peak):
    noise = random.Random({"charge": 734, "release": 735, "shockwave": 736, "arrival": 737,
                          "defeat_laugh": 738}[name])
    samples = array("f")
    filtered = phase = 0.0
    laugh_pulses = ((.06, .36, 116), (.54, .40, 105), (1.06, .44, 94), (1.65, .58, 83))
    # Harmonics shaped around a deep /ah/ vowel, rather than a single musical oscillator.
    laugh_harmonics = {
        frequency: [(harmonic, (.10 + sum(gain * math.exp(-.5 * ((harmonic * frequency - center) / width) ** 2)
                                         for center, width, gain in ((560, 95, 1.0), (1030, 150, .68), (2320, 210, .26))))
                     / harmonic ** .72) for harmonic in range(1, 33)]
        for _, _, frequency in laugh_pulses
    } if name == "defeat_laugh" else {}
    for i in range(round(seconds * RATE)):
        t = i / RATE
        u = t / seconds
        white = noise.uniform(-1, 1)
        filtered += .12 * (white - filtered)
        if name == "charge":
            phase += TAU * (115 + 185 * u * u) / RATE
            energy = math.sin(phase) * (.15 + .55 * u)
            bell = sum(a * math.sin(TAU * f * t + .002 * math.sin(TAU * 7 * t))
                       for a, f in ((.30, 349.23), (.14, 523.25), (.09, 829.4)))
            value = (energy + bell + filtered * .55 * u) * (.25 + .75 * u)
        elif name == "release":
            phase += TAU * (380 * math.exp(-t * 10) + 100) / RATE
            snap = (white - filtered) * .4 * math.exp(-t * 18)
            bell = (math.sin(TAU * 698.46 * t) + .35 * math.sin(TAU * 1143.7 * t))
            value = .55 * math.sin(phase) * math.exp(-t * 8) + snap + .3 * bell * math.exp(-t * 7)
        elif name == "defeat_laugh":
            value = 0.0
            for start, duration, frequency in laugh_pulses:
                dt = t - start
                if not 0 < dt < duration:
                    continue
                progress = dt / duration
                envelope = math.sin(math.pi * progress) ** .72
                voiced = min(1, max(0, (dt - .025) / .035))
                vocal_phase = TAU * frequency * (dt - .14 * dt * dt / duration)
                vocal_phase += .06 * math.sin(TAU * 19 * dt)
                voice = sum(gain * math.sin(harmonic * vocal_phase)
                            for harmonic, gain in laugh_harmonics[frequency])
                rough = .83 + .12 * math.sin(TAU * 31 * dt) + .05 * white
                chest = .12 * math.sin(vocal_phase * .5)
                breath = .33 * (white - filtered) * math.exp(-dt * 25)
                value = envelope * (voiced * (voice * rough + chest) + breath)
                break
            # Quiet, decaying reflections leave room for gameplay and the respawn screen.
            for delay, gain in ((.173, .17), (.337, .10), (.521, .05)):
                previous = i - round(delay * RATE)
                if previous >= 0:
                    value += gain * samples[previous]
        elif name == "arrival":
            # Fifty-tick entrance: energy gathers, a gold chord rings, then a soft landing.
            phase += TAU * (43 + 44 * min(t / 1.2, 1) ** 2) / RATE
            gather = math.sin(math.pi * min(t / 1.65, 1)) ** 2
            energy = (.32 * math.sin(phase) + .12 * math.sin(phase * 1.501)
                      + .13 * filtered) * gather
            bell_time = max(0, t - 1.08)
            bell_attack = 1 - math.exp(-bell_time * 45)
            bell = sum(a * math.sin(TAU * f * bell_time) * math.exp(-bell_time * decay)
                       for a, f, decay in ((.38, 349.23, 2.1), (.22, 523.25, 2.6),
                                           (.12, 698.46, 3.1), (.07, 1117.54, 4.0))) * bell_attack
            pulse_time = max(0, t - 1.98)
            pulse_attack = 1 - math.exp(-pulse_time * 65)
            pulse_phase = TAU * (39 * pulse_time + 40 * (1 - math.exp(-pulse_time * 11)) / 11)
            pulse = (.63 * math.sin(pulse_phase) + .18 * filtered) * pulse_attack * math.exp(-pulse_time * 9)
            value = energy + bell + pulse
        else:
            phase += TAU * (95 * math.exp(-t * 5) + 38) / RATE
            gong = (math.sin(TAU * 174.61 * t) + .45 * math.sin(TAU * 277.18 * t)
                    + .2 * math.sin(TAU * 493.88 * t)) * math.exp(-t * 3.8)
            value = .70 * math.sin(phase) * math.exp(-t * 4) + .38 * gong + filtered * .9 * math.exp(-t * 9)
        # Raised-cosine fades preserve a quick impact without endpoint clicks.
        fade_in = .5 - .5 * math.cos(math.pi * min(1, t / .012))
        fade_out = .5 - .5 * math.cos(math.pi * min(1, (seconds - t) / .09))
        samples.append(value * fade_in * fade_out)
    scale = peak / max(abs(value) for value in samples)
    return array("h", (round(value * scale * 32767) for value in samples))


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--only", choices=[cue[0] for cue in CUES], help="Leave every other OGG untouched")
    args = parser.parse_args()
    ffmpeg, ffprobe = shutil.which("ffmpeg"), shutil.which("ffprobe")
    if not ffmpeg or not ffprobe:
        raise SystemExit("ffmpeg and ffprobe are required")
    DEST.mkdir(parents=True, exist_ok=True)
    for name, duration, peak in CUES:
        if args.only and name != args.only:
            continue
        pcm = synth(name, duration, peak)
        assert len(pcm) == round(duration * RATE) and pcm[0] == 0
        with tempfile.TemporaryDirectory(prefix="archmage-audio-") as temporary:
            wav = Path(temporary) / f"{name}.wav"
            with wave.open(str(wav), "wb") as output:
                output.setparams((1, 2, RATE, 0, "NONE", "not compressed"))
                output.writeframes(pcm.tobytes())
            target = DEST / f"{name}.ogg"
            subprocess.run([ffmpeg, "-y", "-v", "error", "-i", str(wav), "-map_metadata", "-1",
                            "-ac", "1", "-ar", str(RATE), "-c:a", "libvorbis", "-q:a", "5", str(target)], check=True)
        metadata = json.loads(subprocess.check_output([ffprobe, "-v", "error", "-show_streams", "-of", "json", str(target)]))["streams"]
        assert len(metadata) == 1
        stream = metadata[0]
        assert stream["channels"] == 1 and int(stream["sample_rate"]) == RATE and stream["codec_name"] == "vorbis"
        assert abs(float(stream["duration"]) - duration) < .001
        decoded = array("f", subprocess.check_output([ffmpeg, "-v", "error", "-i", str(target), "-f", "f32le", "pipe:1"]))
        decoded_peak = max(abs(value) for value in decoded)
        assert all(math.isfinite(value) for value in decoded) and .1 < decoded_peak < .98
        if name in ("arrival", "defeat_laugh"):
            assert decoded_peak < .9
        assert abs(len(decoded) / RATE - duration) < .01
        assert max(abs(value) for value in decoded[-32:]) < .01
        print(json.dumps({"file": str(target.relative_to(ROOT)), "seconds": duration, "channels": 1,
                          "rate": RATE, "decoded_peak": round(decoded_peak, 5), "bytes": target.stat().st_size,
                          "sha256": hashlib.sha256(target.read_bytes()).hexdigest()}))


if __name__ == "__main__":
    main()
