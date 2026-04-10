package io.github.hello09x.fakeplayer.core.config;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record PresetDefinition(@NotNull List<PresetCommandEntry> commands, @Nullable String inventoryBase64, @Nullable Double reach) {

    public PresetDefinition {
        super();
    }
}
