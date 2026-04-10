package io.github.hello09x.devtools.core.utils;

import com.google.common.base.Throwables;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Exceptions {

    public Exceptions() {
        super();
    }

    public static void suppress(@NotNull Plugin plugin, @NotNull Runnable runnable) {
        suppress(plugin, runnable, true);
    }

    public static void suppress(@NotNull Plugin plugin, @NotNull Runnable runnable, boolean doLog) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            if (doLog) {
                plugin.getLogger().warning(Throwables.getStackTraceAsString(throwable));
            }
        }

    }
}
