> Historical report for this version. Current behavior and verification: [QA-0.30.md](QA-0.30.md).

# 0.28 — spell presentation and research

## Delivered behavior

- All 45 registered spell scrolls have a stable presentation identity. Nine families use different colors, sigils, particle motifs and charge/release sounds. Per-scroll signatures vary tint, rune geometry and pitch.
- Charging draws full-bright, counter-rotating seals at the feet and casting hand. Successful casts send one small visual event; the client animates an expanding seal and family-specific particles. Direct scrolls and selected Mystic Code scrolls share the same identity. Ground boundary placement and successful special scroll paths also emit feedback.
- Existing spell/projectile/DOOM renderers remain in use. This update improves the shared casting presentation; it does not claim 45 completely new bespoke impact animations.
- Client presentation is bounded: up to eight visible charging casters and eight release seals per frame, 32 pending release events, and a particle budget of 360 per tick (80 with Minimal particles). These are implementation limits, not an FPS benchmark.
- Eighteen original synthesized charge/release sounds plus two mechanical DOOM horns are included. DOOM arrival uses a 3.8-second horn; detonation uses a 2.6-second variant. Each horn plays once at the black hole position. These are new synthesized effects inspired by mechanical science-fiction foghorns, not recordings taken from *War of the Worlds*. Generation is reproducible with `scripts/generate_spell_audio.py` and FFmpeg.
- The book now includes an 82-node research map: starting tools, powders, cloth, mana awakening/growth, eight spell families, all 53 ritual recipe studies, a real familiar summon, weapon acquisitions, lake and DOOM branches. Nodes have prerequisite links and done/available/locked states, descriptions, chapter search, dragging, zoom and catalogue links.
- Research guides existing mechanics without adding crafting restrictions. Acquisitions are remembered even before prerequisites are completed. Recipe studies use the existing half-ingredient discovery rule. Completion is saved per player in the persistent, copy-on-death guide attachment.

## Verification performed

- `build visualCheckClasses runProgressionChecks runEconomyChecks` passed on the final gameplay/audio candidate. Native tests cover all scroll signatures; all 20 Ogg assets; sound registration; visual event serialization; graph IDs, item references, prerequisite reachability and coverage of 53 rituals; early acquisition credit; a real familiar summon; and persistent-data codec round trips.
- The native DOOM observer at 96 blocks received exactly one arrival horn and one impact horn from the target position, in addition to the preserved heartbeat/charge/impact effects.
- Existing server checks passed, including 15,440 combat matrix cases, 1,372 ordered catalyst interactions, claims/destruction checks and progression regressions. This does not constitute a new manual playthrough of every combat combination.
- Final packaged JAR plus Fabric API launched twice on a copy of the original world. Battery mana and Reality Marble survived restart; original world hashes remained unchanged. No development source sets were required.
- Inspected live screenshots of the research map, familiar charge/release at night, first-person eye magic and purple DOOM charging. The native sound engine accepted all custom sound events. Native search widget, wheel zoom and returning to the catalogue passed their client check.
- The actual Minecraft 26 save under `players/data/` contains the research attachment, including the real familiar milestone. After a clean preview-server restart the client map retained completed research.

Local verification logs are under the workspace's ignored `analysis/phase029-*` files. The original user worlds were preserved. The isolated village preview world remains available for testing.
