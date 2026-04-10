package io.github.hello09x.fakeplayer.core.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public record ArenaSpawn(@NotNull String world, double x, double y, double z, float yaw, float pitch) {

    public ArenaSpawn {
        super();
    }

    public @NotNull Location toLocation() {
        World w = Bukkit.getWorld(this.world);

        if (w == null) {
            w = (World) Bukkit.getWorlds().get(0);
        }

        return new Location(w, this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public static @NotNull ArenaSpawn fromLocation(@NotNull Location location) {
        return new ArenaSpawn(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
}
