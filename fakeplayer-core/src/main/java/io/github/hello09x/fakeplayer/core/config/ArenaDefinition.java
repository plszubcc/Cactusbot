package io.github.hello09x.fakeplayer.core.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ArenaDefinition(@NotNull ArenaSpawn playerSpawn, @NotNull ArenaSpawn botSpawn, @Nullable String preset) {
}
