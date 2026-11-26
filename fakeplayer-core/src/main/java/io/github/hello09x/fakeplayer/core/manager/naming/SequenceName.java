package io.github.hello09x.fakeplayer.core.manager.naming;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public record SequenceName(@NotNull String group, int sequence, @NotNull UUID uuid, @NotNull String name) {
}
