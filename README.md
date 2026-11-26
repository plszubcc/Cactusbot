# CactusBot

CactusBot is a **Paper/Spigot 1.21.11** fake-player plugin. It spawns server-side "bots" that can be controlled with commands and used for PvP practice, arenas, queues, recording/replay, and automation.

This repository is the **recovered source** of the `Bot +.jar` build (plugin version `0.3.19`). The original source was lost; the code was reconstructed from the compiled jar with [Vineflower](https://github.com/Vineflower/vineflower), then cleaned up until the project builds again. See [docs/RECOVERY.md](./docs/RECOVERY.md) for details.

## Features

- Spawn and control fake players (`/cb spawn`, `/cb kill`, `/cb list`, ...)
- Movement, look, attack, crit, mace, mine, use, place, jump, sneak, sprint, ride, sleep, ...
- PvP arenas, match queues and gamemodes
- Presets with command chains, conditions, inventory snapshots and cooldowns
- Bot sounds and a play GUI
- Recording and replay of player actions
- PlaceholderAPI, SkinsRestorer and OpenInv integration
- Persistent profiles and user settings

## Requirements

- Paper (or Spigot) **1.21.11**
- **Java 21**
- [CommandAPI](https://commandapi.jorel.dev/) 11.x

## Build

See [BUILD.md](./BUILD.md). Short version:

```bash
mvn clean package -DskipTests
```

The shaded plugin is written to `target/fp-1.jar`.

> The NMS module targets `1.21.11-R0.2-SNAPSHOT` and needs a locally installed remapped Spigot artifact. Build it once with BuildTools:
> `java -jar BuildTools.jar --rev 1.21.11 --remapped`

## Install

1. Build the jar (or download it from Releases).
2. Put `fp-1.jar` in your server's `plugins/` folder.
3. Install CommandAPI.
4. Start the server and edit `plugins/cactusbot/config.yml`.

## Commands and permissions

The command root is `/cactusbot` with the alias `/cb`. See [`plugin.yml`](./fakeplayer-core/src/main/resources/plugin.yml) for the full permission list.

## Project layout

| Module | Purpose |
| --- | --- |
| `fakeplayer-api` | SPI interfaces shared by the core and NMS modules |
| `fakeplayer-core` | Plugin logic, commands, configuration and storage |
| `fakeplayer-v1_21_11` | NMS implementation for Minecraft 1.21.11 |
| `fakeplayer-dist` | Shaded distributable jar |

## Credits

- Original **FakePlayer** by [hello09x / tanyaofei](https://github.com/tanyaofei/minecraft-fakeplayer)
- Fork by [coderxi1](https://github.com/coderxi1/minecraft-fakeplayer)
- Source recovery and CactusBot customizations

## License

Apache License 2.0. See [LICENSE](./LICENSE).
