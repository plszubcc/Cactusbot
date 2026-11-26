package io.github.hello09x.fakeplayer.core.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record PvpArenaDefinition(
   @NotNull String name,
   @Nullable String preset,
   @NotNull String botName,
   @Nullable String botSkin,
   @Nullable String playerInventoryBase64,
   int rounds,
   @NotNull ArenaSpawn playerSpawn,
   @NotNull ArenaSpawn botSpawn,
   boolean busy
) {
}
