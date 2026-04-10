package io.github.hello09x.devtools.core.utils;

import java.util.Objects;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class WorldUtils {

    private static final String WORLD_OVERWORLD = "world";
    private static final String WORLD_NETHER = "world_nether";
    private static final String WORLD_END = "world_the_end";

    public WorldUtils() {
        super();
    }

    public static boolean isOverworld(@NotNull World world) {
        return world.getName().equals("world");
    }

    public static @NotNull World getOverworld() {
        return (World) Objects.requireNonNull(Bukkit.getWorld("world"), "No world named '%s' on this server".formatted("world"));
    }

    public static @NotNull World getNetherWorld() {
        return (World) Objects.requireNonNull(Bukkit.getWorld("world_nether"), "No world named '%s' on this server".formatted("world_nether"));
    }

    public static @NotNull World getTheEndWorld() {
        return (World) Objects.requireNonNull(Bukkit.getWorld("world_the_end"), "No world named '%s' on this server".formatted("world_the_end"));
    }

    public static @Nullable World getNonOverworld() {
        return (World) Bukkit.getWorlds().stream().filter((wx) -> {
            return !wx.getName().equals("world");
        }).findAny().orElse((Object) null);
    }

    public static @Nullable World getOtherWorld(@NotNull World notThisWorld) {
        return (World) Bukkit.getWorlds().stream().filter((wx) -> {
            return !wx.equals(notThisWorld);
        }).findAny().orElse((Object) null);
    }

    public static @NotNull World getMainWorld() {
        return (World) Optional.ofNullable(Bukkit.getWorld("world")).orElseGet(() -> {
            return (World) Bukkit.getWorlds().get(0);
        });
    }
}
