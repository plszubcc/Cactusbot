package io.github.hello09x.devtools.core.utils;

import java.util.List;
import java.util.Optional;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MetadataUtils {

    public MetadataUtils() {
        super();
    }

    public static @NotNull Optional<MetadataValue> find(@NotNull Plugin plugin, @Nullable Metadatable metadatable, @Nullable String metadataKey, @NotNull Class<?> type) {
        if (metadatable == null) {
            return Optional.empty();
        } else if (metadataKey == null) {
            return Optional.empty();
        } else {
            List<MetadataValue> values = metadatable.getMetadata(metadataKey);

            return values.isEmpty() ? Optional.empty() : values.stream().filter((vx) -> {
                return vx.getOwningPlugin() == plugin && type.isInstance(vx.value());
            }).findAny();
        }
    }
}
