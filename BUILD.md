# Building CactusBot

## Prerequisites

- JDK 21
- Maven 3.9+
- [BuildTools](https://www.spigotmc.org/wiki/buildtools/) (only needed for the NMS module)

## 1. Install the remapped Spigot artifact

Mojang does not allow the remapped NMS jar to be published to a public Maven repository, so Maven needs a local copy:

```bash
mkdir buildtools
cd buildtools
curl -O https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
java -jar BuildTools.jar --rev 1.21.11 --remapped
```

This installs `org.spigotmc:spigot:1.21.11-R0.2-SNAPSHOT` with the `remapped-mojang` classifier into `~/.m2/repository`.

> **Troubleshooting:** if the artifact was previously installed manually with `install:install-file`, the generated POM may be empty. Maven will then miss transitive dependencies such as Netty, authlib and DataFixerUpper. Re-run BuildTools, or restore the real POM embedded in the jar at `META-INF/maven/org.spigotmc/spigot/pom.xml`.

## 2. Build

```bash
mvn clean package -DskipTests
```

The build creates:

- `target/fp-1.jar` — shaded plugin ready to drop into `plugins/`
- module jars under `target/`

## 3. Run

1. Copy `target/fp-1.jar` into your server's `plugins/` folder.
2. Install CommandAPI 11.x.
3. Start the server.
4. Configure `plugins/cactusbot/config.yml`.

## Modules

- `fakeplayer-api` — SPI interfaces
- `fakeplayer-core` — plugin logic, commands, config
- `fakeplayer-v1_21_11` — NMS implementation for 1.21.11
- `fakeplayer-dist` — shaded distributable jar
