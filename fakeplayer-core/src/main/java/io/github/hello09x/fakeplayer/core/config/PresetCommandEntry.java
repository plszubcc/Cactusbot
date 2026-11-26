package io.github.hello09x.fakeplayer.core.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record PresetCommandEntry(
   @Nullable String id, @NotNull String command, int repeatTicks, int delayTicks, int stopTicks, int limit, @Nullable PresetConditions conditions
) {
}
