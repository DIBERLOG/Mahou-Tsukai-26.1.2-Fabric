# Verify build 0.32

Two verification tracks: the native dedicated-server suite (repeatable by anyone) and the real-client
track (run by the user). The native track never touches worlds under `run/server`.

## 1. Native checks and the JAR hash

```powershell
powershell -ExecutionPolicy Bypass -File scripts/verify_032.ps1
```

The script uses the project JDK, runs `compileJava/compileClientJava/selfCheckClasses`,
`runProgressionChecks`, `runEconomyChecks` and `build`, then prints the built JAR path, size and
SHA-256. Success is judged by the run markers, not only by the Gradle exit code:

- `analysis/phase026-progression-server.passed` (written by `runProgressionChecks`)
- `MahouTsukai-Fabric/run/economy-checks/economy-checks.passed` (written by `runEconomyChecks`)

Expected headline lines in the logs:

- `PASS: PROGRESSION_SERVER_026` with `REALITY_MARBLE_BOSS`, `WILLIAM_FLIGHT`,
  `GAUNTLET_FLIGHT`, `PRESENTATION_RESEARCH`, `MENTAL_DISPLACEMENT_032`, `NOBU_031`,
  `FIREBALL_031`, `GRAVITY_027`, `PROGRESSION_027`.
- `PASS: ECONOMY_INTEGRATION` with `DOOM_INTEGRATION`, `TREASURY_PROJECTION_INTEGRATION`,
  `MYSTIC_STAFF_INTEGRATION`, `REALITY_MARBLE_INTEGRATION`, `ENTITIES_PROJECTORS`,
  `WILLIAM_ITEM`, `SCENARIO_PARITY` (the new boss entity is recorded as an intentional addition).

If the markers are stale or missing, treat the run as failed even when Minecraft exits with 0.

## 2. Client track

Prerequisites: Java 25, Fabric Loader >= 0.19.5, Fabric API >= 0.155.3. The Gradle runs provide the
mod and the visual-check harness.

1. Start the dedicated test server in one terminal (uses `run/server`, level
   `mahou-weapons-20261018`, port 25585):

   ```powershell
   cd MahouTsukai-Fabric
   .\gradlew.bat runServer
   ```

   Wait for `Done (...)` in the server log.

2. Start the client in a second terminal. `magicVisual` auto-connects to `127.0.0.1:25585` at
   1280×720 and loads the capture harness:

   ```powershell
   .\gradlew.bat magicVisual
   ```

3. The harness reacts to a request file in the client run directory. Write one scenario name, and it
   performs the action and writes `<name>.passed` next to it:

   ```powershell
   Set-Content -Path MahouTsukai-Fabric/run/client/visual-capture.request -Value hud-placement-check
   ```

### Scenarios added for 0.32

| Request | What it proves | Receipt |
| --- | --- | --- |
| `hud-placement-check` | absurd `CLIENT_GUI_MAHOU_PLACEMENT_*` is clamped to the screen | `hud-placement-check.passed` |
| `catalog-showrecipe-check` | research→recipe jump clears the search and selects the real compendium item (open the catalogue first) | `catalog-showrecipe-check.passed` |
| `translations-032-check` | all new 0.32 texts resolve in the active language | `translations-032-check.passed` |

Useful existing scenarios for the same build: `hints-toggle-check`, `research-controls-check`,
`research-open` then `research-doom`/`research-fireball`, `handbook-open`, `doom-visual-check`,
`stage-doom-growth`, `stage-doom-large`, `stage-doom-shockwave`, `stage-nobu-guns`,
`stage-mystic-hand`, `presentation-audio-check`, `fog-camera-check`.

### Manual visual checklist (screenshots)

| Check | How | Expected |
| --- | --- | --- |
| Mana HUD | join, look top-left | current/max mana, mage level, cast hints, cooldown lines |
| HUD hidden hint | `/showmahou` (hide), relog | one chat hint explaining how to restore |
| Cooldowns | cast Fireball, then DOOM | remaining seconds and readiness line |
| Catalogue / research | right-click the compendium | research→recipe opens the item without a technical id; long text scrolls with the wheel |
| Icons | open catalogue/tree | Alchemical/Chronal/Equivalent/Ordered/Scrying show the ritual glyph, not an empty slot |
| Projectors | creative catalogue | service blocks visible with an explanation; no survival recipe; old builds still work |
| DOOM | `/give @s mahoutsukai:scroll_doom`, aim 96 blocks, hold 3 s | rumble + heartbeat, sky darkens locally, blocks erode during the pull, big final blast |
| Gandr | `/give @s mahoutsukai:scroll_gandr`, hold | one coherent orb with converging sparks; no oval ring or rotating ribbon; first-person camera stays clear |
| Staff big mode | `/give @s mahoutsukai:scroll_mystic_staff`, mode 0 | red charge circles, one blast sound (not one per player), strong impact |
| Gauntlet | `/give @s mahoutsukai:treasury_projection_gauntlet` | swords fly straight, home slightly, hit once and vanish; walls stop them |
| Spiritual displacement | `/give @s mahoutsukai:scroll_mental_displacement`, hold | countdown in HUD, LMB ends travel and teleports you to the spirit |
| Nobu | `/give @s mahoutsukai:nobu`, sneak+right-click mode change | old constructs vanish at once; only yours |
| William | `/give @s mahoutsukai:william` or a library chest | book hovers and rotates smoothly |
| Reality Marble boss | `/give @s mahoutsukai:scroll_reality_marble`, use it | one boss per arena, boss bar, wind-up pulse before the special, Morgan on victory once per player |

## 3. What cannot be claimed from one run

- Visual parity for every combination, third-party shaders and mods is not claimed.
- The native fixture cannot damage its fake players; boss damage to real players must be confirmed in
  game (the same damage source is proven on a creature in `REALITY_MARBLE_BOSS`).
- `hud-placement-check` proves the clamp; it does not prove that the HUD was visible for a specific
  player before the fix.

Do not publish `run/server` logs, server settings or the local configuration: they contain local
paths and server credentials. Only the JAR, `SHA256SUMS.txt` and the docs are meant to be shared.
