# 0.29 — film sound and research badges

- DOOM arrival and impact now reference the short film recording listed in [SOUND-SOURCES.md](SOUND-SOURCES.md). The previous synthesized horns are removed. Both events play at pitch 1, with original timing; the recording has −4 dB gain and short edge fades. Measured final true peak: −3.19 dBTP. The resource fully decodes as mono Vorbis.
- Research states use drawn badges: a white check on a mint medallion for completed nodes, a gold sparkle for available nodes, and a padlock for prerequisites. The selected-node status uses the same badge, and widget narration includes the written state.
- Ground rituals without inventory items receive a drawn ritual symbol instead of an empty icon.

Verification: build, focused progression checks and the full native economy/combat/ritual runner passed. The DOOM test asserts one arrival and one impact event at the target, audible to a 96-block observer, and unchanged pitch. The asset mapping test verifies both events reference the actual film resource. Native client audio resolution, research search, zoom and catalogue return passed. Fresh in-game screenshots were inspected.

At the time of the 0.29 checks, no public upload was performed. The later publication is covered by [QA-0.30.md](QA-0.30.md). Source provenance is recorded separately; the film excerpt is not described as original synthesized audio.
