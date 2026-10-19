# Guide update — 2026-10-19

- Added a full handbook with 114 chapters, chapter-title search, scrollable text and a return to the recipe catalogue. Original ritual articles retain discovery gating.
- Added Russian/English instructions for familiars, the distinction between Reality Marble and a village, and the custom progression/casting rules.
- Catalogue ritual results now come from the actual block entity's `ScrollGiver`, including names that differ from their block identifier. Entries without ingredient recipes are readable rather than permanently locked behind an impossible discovery threshold.
- Restored seven original mortar-and-pestle recipes: each makes two powders. All 33 original crafting recipe resources are present; the only additional recipes are sculk powder and DOOM. The 26 previously shared recipes had identical ingredient/result/pattern content after normalizing native ingredient syntax and display metadata.

Validation: `build visualCheckClasses runProgressionChecks` passed. Added native assertions that all seven mortar recipes appear with output count two and every nonempty ritual `ScrollGiver` output appears in the catalogue. Existing progression, rain and gravity checks also passed.

Live client: catalogue received from the local server and handbook opened successfully; screenshots `phase028-familiar-catalog.png` and `phase028-handbook.png` were inspected. A first UI automation attempt used an underscore forbidden by the capture tool's command format; corrected the command and repeated successfully.

Prepared a separate world `mahou-guide-village-20261019` in the isolated preview server; located a plains village at X=32, Z=288 and placed the player at the terrain height, Y=65. Existing world directories retained. Gave the player the guide, quartz powders, cloth and a familiar scroll.

Limit: original prose is preserved and can quote original prices; the handbook explicitly directs players to the catalogue and casting HUD for current costs. This is not a new exhaustive manual playthrough of every original article or battle combination.
