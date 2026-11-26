package io.github.hello09x.fakeplayer.core.config;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public record CustomActionDefinition(@NotNull List<PresetCommandEntry> commands) {
}
