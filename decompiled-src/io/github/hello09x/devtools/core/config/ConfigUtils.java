package io.github.hello09x.devtools.core.config;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ConfigUtils {

    public ConfigUtils() {
        super();
    }

    public static int getMaxIfZero(int value) {
        return value == 0 ? 2147483647 : value;
    }

    public static <E extends Enum<E>> E getEnum(@Nullable String name, @NotNull Class<E> clz, E defaultValue) {
        return (E) (name != null && !name.isEmpty() ? Enum.valueOf(clz, name) : defaultValue);
    }

    public static <E extends Enum<E>> E getEnum(@NotNull FileConfiguration config, @NotNull String key, @NotNull Class<E> type, @Nullable E defaultValue) {
        String value = config.getString(key);

        if (value != null && !value.isEmpty()) {
            try {
                return (E) Enum.valueOf(type, value);
            } catch (IllegalArgumentException illegalargumentexception) {
                throw new IllegalArgumentException("The value of %s in config.yml is invalid, allowed values are %s".formatted(key, Arrays.stream((Enum[]) type.getEnumConstants()).map(Enum::name).collect(Collectors.joining(", "))), illegalargumentexception);
            }
        } else {
            return defaultValue;
        }
    }
}
