# Source recovery notes

The source in this repository was recovered from the compiled `Bot +.jar` (plugin `cactusbot` v0.3.19, built 2025-11-06). The original source was lost.

## Method

1. Unpacked the jar and listed the plugin's own packages (`io.github.hello09x.fakeplayer.*`, `com.coderxi.*`).
2. Decompiled the class files with Vineflower 1.11.1 (`-dgs=1 -hdc=0`).
3. Reconstructed the Maven multi-module layout from the `META-INF/maven/**/pom.xml` files inside the jar.
4. Restored the resources from the jar: `plugin.yml`, `config.yml`, `message/*.properties`.
5. Fixed decompiler artifacts until `mvn package` succeeded.
6. Compared the rebuilt jar with the original: both contain 1,064 entries.

## Fixes applied

- Removed invalid `super()` calls from record compact constructors.
- Restored generic type arguments where Vineflower emitted raw `Object` types.
- Fixed lambda captures that referenced the wrong loop variable.
- Removed an illegal import of the protected `AbstractChannel.AbstractUnsafe` type.
- Added `@SneakyThrows` to `InternalAddressGenerator#next()` (the original used Lombok).
- Added the missing type witness for `CommandAPIBukkit.<CommandSourceStack>get()`.

## Third-party code

The original jar is shaded and contains Guice, HikariCP, SLF4J and the author's `devtools` libraries. Those are **not** included as source; the project depends on them through Maven and JitPack.

## Not included

Server-specific files such as `config-premadebots.yml` (a 3.5 MB preset/inventory config) are not part of the source tree.
