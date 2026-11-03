# KART

Modular karts for NeoForge 26.2.

## What it has

- Chassis, engines, wheels, boosters, and accessories
- Server-side driving with drift, handbrake, boost, and heat
- Install/remove workshop UI
- Component-specific models and terrain-following suspension
- Kart commands: `/kart spawn`, `/kart install`, `/kart remove`, `/kart stats`, `/kart telemetry`

## Controls

- `W`/`S` throttle and brake/reverse
- `A`/`D` steer
- `Space` handbrake
- `Left Ctrl` boost
- `Shift` + right-click a kart to open the workshop
- Right-click a kart to mount

## Build

```bash
sh gradlew build
```

Editable Blockbench source: `blockbench/kart.bbmodel`. After editing it, run:

```bash
python3 scripts/blockbench_to_java.py
sh gradlew build
```

Runs:

```bash
sh gradlew runClient
sh gradlew runServer
```

Components are creative-only for now. Racing and custom audio are not implemented yet.

## License

Code is MIT. Original assets are CC BY 4.0. See `LICENSE`, `LICENSE-ASSETS`, and `CREDITS.md`.
