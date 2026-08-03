# IMindustry

**IMindustry** — experimental Mindustry Java mod focused on making early/mid game turrets more interesting and useful.

## Current features

### Duo
- **Titanium** ammo: 2× damage of Silicon, half fire rate, applies Slow, slightly longer range
- **Sand** ammo: cheap, lower damage, strong knockback (great for crowd control when stacked)

### Scatter
- **Silicon** ammo: homing flak round that bursts into 6 shrapnel fragments on hit (excellent vs fast/clustered air units)

## Planned
- Hail: Titanium (faster + freeze) and Thorium (stacking radiation effect → explosion on death, scaled by stacks, max ~1/4 of real Thorium Reactor)
- Overheat mechanic for Thorium ammo on Hail

## Building

### Desktop only
```bash
./gradlew jar
```
Output: `build/libs/IMindustryDesktop.jar`

### Cross-platform (Desktop + Android)
Requires Android SDK + `d8` in PATH.
```bash
./gradlew deploy
```
Output: `build/libs/IMindustry.jar`

## Installation
Put the jar into your Mindustry `mods/` folder.

- Desktop: `%AppData%/Mindustry/mods/` (Windows) or `~/.local/share/Mindustry/mods/` (Linux)
- Android: `Android/data/io.anuke.mindustry/files/mods/`

## Author
IConanEdogawa
