package io.github.hello09x.devtools.core.utils;

import org.bukkit.Bukkit;

public class ServerUtils {

    private static final boolean IS_FOLIA = Bukkit.getServer().getName().equals("Folia");

    public ServerUtils() {
        super();
    }

    public static boolean isFolia() {
        return ServerUtils.IS_FOLIA;
    }
}
