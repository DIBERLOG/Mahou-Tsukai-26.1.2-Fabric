# Sound provenance

The eighteen family charge/release sounds in `sounds/cast/` and ninety-two individual scroll sounds in `sounds/spells/` are synthesized by `scripts/generate_spell_audio.py`. Build 0.30 added a distinct charge and release phrase for each of its 45 scrolls; 0.31 adds Fireball's two sounds for 46 scrolls in total. Fireball uses a warm harmonic ignition, a filtered noise roar and ember accents. The family sounds remain fallbacks for the staff and mana-capacity scrolls. The individual clips use synthesized timbres, distinct note phrases and spell-specific envelopes. They do not sample the film recording. The generator's `--only fireball` mode preserves all existing audio resources.

Starting with local build 0.29, the DOOM arrival and impact events use the same **War of the Worlds (2005)** excerpt, identified by its hosting page as “The noise the Alien Tripods make”:

- Source page: https://www.moviesoundclips.net/sound.php?id=79
- Downloaded Ogg: https://www.moviesoundclips.net/download.php?ft=ogg&id=1345
- Original duration: 9.264 seconds; mono Vorbis, 32 kHz.
- Original SHA-256: `1c7f876c26d7bb2cb376231d4685138ed7effcf3b509f952f21de70ff2b2b127`.
- Game resource: `assets/mahoutsukai/sounds/doom/tripod_film.ogg`.
- Processing: −4 dB gain, 25 ms fade-in, 80 ms fade-out, Vorbis re-encoding. Playback speed and pitch are unchanged.

This is a third-party film excerpt included at the user's explicit request. It is not an original synthesized asset, and no open redistribution license is claimed. Publishing this repository or a build does not grant a license to reuse the recording. The synthesis script preserves these externally sourced sound entries.
