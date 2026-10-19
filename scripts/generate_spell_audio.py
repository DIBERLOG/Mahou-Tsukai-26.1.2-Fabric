"""Generate original individual mono cues; preserve fallback and film recordings."""
from array import array
from pathlib import Path
import argparse
import hashlib
import json
import math
import random
import re
import shutil
import struct
import subprocess
import tempfile
import wave

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / 'MahouTsukai-Fabric/src/main/resources/assets/mahoutsukai'
# MIDI root, musical gesture, instrument, breath. Recall descends; ascension climbs;
# replication echoes pairs; shields resolve a chord. Each spell owns its motif.
PROFILES = {
    'insight': (76, (0, 7, 12, 19), 'crystal', .025),
    'prediction': (81, (7, 0, 12, 4), 'bell', .018),
    'cup_of_heaven': (62, (0, 4, 7, 12, 16), 'choir', .035),
    'reversion_eyes': (75, (12, 7, 3, 0), 'crystal', .035),
    'presence_concealment': (56, (7, 3, 0), 'veil', .14),
    'gandr': (45, (0, 1, 7), 'arc', .12),
    'rho_aias': (60, (0, 7, 12, 16), 'choir', .025),
    'fallen_down': (40, (19, 12, 7, 0), 'choir', .11),
    'borrowed_authority': (53, (0, 7, 10, 12), 'steel', .045),
    'mental_displacement': (68, (0, 6, 12, 6), 'veil', .10),
    'possess_entity': (57, (0, 3, 1, 7), 'choir', .06),
    'spatial_disorientation': (66, (0, 6, 1, 13, 7), 'crystal', .085),
    'weapon_shooter': (69, (0, 7, 12), 'steel', .055),
    'treasury_projection': (58, (0, 4, 7, 12, 19), 'steel', .04),
    'reality_marble': (48, (0, 7, 14, 21), 'choir', .075),
    'mystic_staff': (52, (0, 7, 12, 24), 'arc', .07),
    'butterfly_effect': (84, (0, 7, 4, 12, 9), 'leaf', .035),
    'selective_displacement': (70, (0, 12, 6), 'veil', .06),
    'damage_replication': (55, (0, 0, 7, 7), 'arc', .07),
    'proximity_projection': (74, (0, 3, 7, 15), 'steel', .04),
    'summon_familiar': (72, (0, 4, 7, 12), 'leaf', .04),
    'recall_familiar': (79, (12, 7, 4, 0), 'leaf', .025),
    'swap_familiar': (77, (0, 12, 0, 7), 'bell', .03),
    'familiars_garden': (67, (0, 4, 9, 14, 19), 'leaf', .065),
    'black_flame': (43, (0, 3, 6, 12), 'void', .18),
    'projection': (71, (0, 7, 16), 'bell', .025),
    'strengthening': (50, (0, 5, 7, 12), 'steel', .065),
    'death_collection': (46, (12, 7, 1, 0), 'void', .09),
    'projectile_displacement': (82, (0, 12, 7), 'arc', .055),
    'retribution': (47, (0, 6, 7, 12), 'steel', .095),
    'mystic_eyes': (64, (0, 1, 7, 8), 'crystal', .045),
    'boundary_tangible': (59, (0, 4, 7), 'bell', .045),
    'boundary_raise_enclosure': (54, (0, 5, 12, 17), 'steel', .055),
    'boundary_drain_life': (51, (12, 8, 3, 0), 'void', .10),
    'boundary_displacement': (73, (0, 7, 1, 13), 'veil', .075),
    'boundary_gravity': (38, (12, 7, 3, -5), 'void', .12),
    'boundary_alarm': (85, (0, 7, 0, 12), 'bell', .018),
    'protective_displacement': (65, (0, 7, 12, 7), 'choir', .055),
    'immunity_exchange': (70, (0, 4, 12, 7), 'leaf', .035),
    'damage_exchange': (61, (0, 3, 10, 7), 'arc', .08),
    'geas': (49, (0, 7, 6, 0), 'choir', .085),
    'probability_alter': (80, (0, 6, 11, 4, 12), 'crystal', .04),
    'doom': (30, (0, 1, 7, 13), 'void', .24),
    'fay_sight': (88, (0, 4, 7, 16), 'leaf', .015),
    'ascension': (63, (0, 7, 12, 19, 24), 'choir', .07),
    'fireball': (49, (0, 7, 10, 19), 'flame', .22),
}


def voice(phase, age, instrument):
    if instrument == 'bell':
        return (math.sin(phase) + .38 * math.sin(phase * 2.006) * math.exp(-age * 4)
                + .17 * math.sin(phase * 4.03) * math.exp(-age * 8))
    if instrument == 'crystal':
        return math.sin(phase + .9 * math.sin(phase * 2.01) * math.exp(-age * 6)) + .16 * math.sin(phase * 3.98)
    if instrument == 'steel':
        return (math.sin(phase) + .40 * math.sin(phase * 1.48) * math.exp(-age * 6)
                + .23 * math.sin(phase * 2.76) * math.exp(-age * 10))
    if instrument == 'choir':
        return (.8 * math.sin(phase + .07 * math.sin(age * 31)) + .34 * math.sin(phase * 2)
                + .17 * math.sin(phase * 3) + .07 * math.sin(phase * 5))
    if instrument == 'arc':
        return math.sin(phase + 1.8 * math.sin(phase * .5) * math.exp(-age * 7)) + .19 * math.sin(phase * 3)
    if instrument == 'void':
        return .65 * math.sin(phase * .5) + math.sin(phase + .8 * math.sin(phase * 1.03)) + .13 * math.sin(phase * 2.03)
    if instrument == 'veil':
        return .65 * math.sin(phase + .5 * math.sin(age * 19)) + .21 * math.sin(phase * 1.501)
    if instrument == 'flame':
        return (math.sin(phase + .08 * math.sin(age * 39)) + .24 * math.sin(phase * 2)
                + .12 * math.sin(phase * 3) + .07 * math.sin(phase * 5))
    return math.sin(phase) + .20 * math.sin(phase * 2) + .06 * math.sin(phase * 3)


def render(name, release):
    root, motif, instrument, air = PROFILES[name]
    rate = 44100
    # Short cues leave the hold timer audible and readable; activation gets a longer tail.
    duration = (2.15 if release else 1.08) + (len(motif) - 3) * .08
    rng = random.Random(name + str(release))
    fundamental = 440 * 2 ** ((root - 69) / 12)
    step = .10 if release else .14
    notes = [(j * step, fundamental * 2 ** (semitones / 12)) for j, semitones in enumerate(motif)]
    samples, breath = [], 0
    for i in range(round(duration * rate)):
        t = i / rate
        breath = .92 * breath + .08 * rng.uniform(-1, 1)
        value = 0
        for start, frequency in notes:
            age = t - start
            if age < 0:
                continue
            if release:
                envelope = (1 - math.exp(-age * 160)) * math.exp(-age * (3.2 if instrument == 'choir' else 4.5))
                phase = 2 * math.pi * frequency * (age + .0025 * (1 - math.exp(-age * 24)))
            else:
                envelope = (1 - math.exp(-age * 35)) * math.exp(-age * 4.5)
                phase = 2 * math.pi * frequency * (.74 * age + .17 * age * age)
            value += .20 * voice(phase, age, instrument) * envelope
        swell = math.sin(math.pi * min(t / duration, 1)) ** 1.5
        value += air * breath * swell * (1.0 if release else .7)
        if instrument == 'flame':
            # A rising ignition gives way to a short fire-breath roar and embers.
            flame = ((1 - math.exp(-t * 95)) * math.exp(-t * 3.6) if release
                     else swell * (.25 + .75 * min(t / .8, 1)))
            value += (.45 * breath + .035 * rng.uniform(-1, 1)) * flame
        if release:
            value += .16 * math.sin(2 * math.pi * (fundamental * .5 * t + .9 * (1 - math.exp(-t * 24)))) * math.exp(-t * 14)
        else:
            value += .06 * math.sin(2 * math.pi * fundamental * (1.25 * t + .35 * t * t)) * swell
        samples.append(value)
    dry = samples[:]
    for delay, gain in ((.11, .24), (.237, .12), (.389, .065)):
        shift = round(delay * rate)
        for i in range(shift, len(samples)):
            samples[i] += dry[i - shift] * gain
    peak = max(map(abs, samples)) or 1
    fade = round(rate * .045)
    for i in range(fade):
        samples[i] *= i / fade
        samples[-i - 1] *= i / fade
    # -4.4 dBFS leaves headroom for lossy-encoding overshoot and positional mixing.
    return rate, b''.join(struct.pack('<h', round(x / peak * 19660)) for x in samples)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--only', choices=sorted(PROFILES), nargs='+',
                        help='Generate only these scrolls without replacing other audio files')
    selected = set(parser.parse_args().only or PROFILES)
    ffmpeg = shutil.which('ffmpeg')
    if not ffmpeg:
        raise SystemExit('ffmpeg is required to encode the generated sounds')
    items = (ROOT / 'MahouTsukai-Fabric/src/main/java/stepsword/mahoutsukai/item/ModItems.java').read_text(encoding='utf-8')
    registered = set(re.findall(r'register\("scroll_([^"]+)",\s*[\w.]+::new\)', items))
    assert registered == set(PROFILES), f'Scroll coverage mismatch: {registered ^ set(PROFILES)}'
    definitions_path = ASSETS / 'sounds.json'
    definitions = json.loads(definitions_path.read_text(encoding='utf-8'))
    changed_keys = {'spell.' + spell + '.' + stage for spell in selected for stage in ('charge', 'release')}
    protected = {key: value for key, value in definitions.items() if key not in changed_keys}
    destination = ASSETS / 'sounds/spells'
    destination.mkdir(parents=True, exist_ok=True)
    hashes, maximum_peak = set(), 0.0
    with tempfile.TemporaryDirectory(prefix='mahou-audio-') as temp:
        for spell in sorted(selected):
            for release in (False, True):
                stage = 'release' if release else 'charge'
                name = spell + '_' + stage
                rate, pcm = render(spell, release)
                digest = hashlib.sha256(pcm).digest()
                assert digest not in hashes, f'Duplicate PCM: {name}'
                hashes.add(digest)
                wav = Path(temp) / (name + '.wav')
                with wave.open(str(wav), 'wb') as writer:
                    writer.setparams((1, 2, rate, 0, 'NONE', 'not compressed'))
                    writer.writeframes(pcm)
                output = destination / (name + '.ogg')
                subprocess.run([ffmpeg, '-y', '-v', 'error', '-i', str(wav), '-map_metadata', '-1',
                                '-fflags', '+bitexact', '-flags:a', '+bitexact', '-c:a', 'libvorbis',
                                '-q:a', '5', str(output)], check=True)
                decoded = subprocess.run([ffmpeg, '-v', 'error', '-i', str(output), '-f', 'f32le',
                                          '-ac', '1', 'pipe:1'], capture_output=True, check=True).stdout
                samples = array('f')
                samples.frombytes(decoded)
                peak = max(map(abs, samples))
                assert 0 < peak < 10 ** (-3 / 20), f'Encoded audio must retain headroom: {name}: {peak}'
                maximum_peak = max(maximum_peak, peak)
                definitions['spell.' + spell + '.' + stage] = {
                    'subtitle': 'mahoutsukai.subtitle.cast.' + stage,
                    'sounds': [{'name': 'mahoutsukai:spells/' + name, 'attenuation_distance': 24}],
                }
    assert protected == {key: value for key, value in definitions.items() if key not in changed_keys}
    definitions_path.write_text(json.dumps(definitions, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    print(f'Generated and decoded {len(hashes)} distinct original cues for {len(selected)} spells; '
          f'maximum decoded peak {20 * math.log10(maximum_peak):.2f} dBFS. '
          'Existing family cues, DOOM recording and other resources preserved.')


if __name__ == '__main__':
    main()
