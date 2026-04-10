package io.github.hello09x.devtools.core.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class LocationUtils {

    public static long toRegionKey(@NotNull Location pos, int length) {
        return toRegionKey((long) pos.getBlockX(), (long) pos.getBlockZ(), length);
    }

    public static long toRegionKey(int x, int z, int length) {
        return toRegionKey((long) x, (long) z, length);
    }

    public static long toRegionKey(long x, long z, int length) {
        return x / (long) length & 4294967295L | (z / (long) length & 4294967295L) << 32;
    }

    public static @NotNull Location toLocation(@NotNull World world, long regionKey, int length) {
        int x = (int) (regionKey & 4294967295L) * length;
        int z = (int) (regionKey >> 32 & 4294967295L) * length;

        return new Location(world, (double) x, 0.0D, (double) z);
    }

    public static @NotNull String toString(@NotNull Location pos) {
        World world = pos.getWorld();

        return world != null ? "([%s]%.1f, %.1f, %.1f)".formatted(world.getName(), pos.getX(), pos.getY(), pos.getZ()) : "(%.1f, %.1f, %.1f)".formatted(pos.getX(), pos.getY(), pos.getZ());
    }

    public static @NotNull Location[] getCirclePoints(@NotNull Location center, double r, int limits) {
        World world = center.getWorld();
        double x = center.getX();
        double y = center.getY();
        double z = center.getZ();
        double m = 6.283185307179586D / (double) limits;
        Location[] points = new Location[limits];

        for (int i = 0; i < limits; ++i) {
            double x1 = x + r * Math.sin(m * (double) i);
            double z1 = z + r * Math.cos(m * (double) i);

            points[i] = new Location(world, x1, y, z1);
        }

        return points;
    }

    private LocationUtils() {
        super();
    }
}
