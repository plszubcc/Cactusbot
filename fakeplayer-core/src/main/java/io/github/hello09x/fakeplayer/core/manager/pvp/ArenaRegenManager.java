package io.github.hello09x.fakeplayer.core.manager.pvp;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.Main;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class ArenaRegenManager {

    private static final int APPLY_BATCH_SIZE = 512;
    private final Map<String, Boolean> running = new ConcurrentHashMap();
    private final Map<String, Location> firstCorners = new ConcurrentHashMap();
    private final Map<String, Location> secondCorners = new ConcurrentHashMap();
    private final Object fileLock = new Object();

    @Inject
    public ArenaRegenManager() {
        this.loadCornersFromConfig();
    }

    public void setCorner(@NotNull String arena, int corner, @NotNull Location location) throws Exception {
        String key = corner == 1 ? "corner1" : "corner2";
        if (corner == 1) {
            this.firstCorners.put(arena, location.clone());
        } else {
            this.secondCorners.put(arena, location.clone());
        }

        this.saveConfigAsync(yml -> yml.set("arenas." + arena + ".region." + key, this.toSimpleCsv(location)));
        ArenaRegion region = this.loadRegion(arena);
        if (region != null) {
            this.captureSnapshot(arena, region);
        }
    }

    public boolean regenerateAsync(@NotNull String arena, @Nullable Runnable onComplete) {
        if (this.running.putIfAbsent(arena, Boolean.TRUE) != null) {
            return false;
        }

        File snapshotFile = this.getSnapshotFile(arena);
        if (!snapshotFile.isFile()) {
            this.running.remove(arena);
            if (onComplete != null) {
                Bukkit.getScheduler().runTask(Main.getInstance(), onComplete);
            }
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try {
                SnapshotData snapshot = this.readSnapshot(snapshotFile);
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.applySnapshot(arena, snapshot, onComplete));
            } catch (Exception exception) {
                this.running.remove(arena);
                if (onComplete != null) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), onComplete);
                }
            }
        });
        return true;
    }

    public boolean hasSnapshot(@NotNull String arena) {
        return this.getSnapshotFile(arena).isFile();
    }

    private void captureSnapshot(@NotNull String arena, @NotNull ArenaRegion region) throws IOException {
        ArrayList<BlockSnapshot> blocks = new ArrayList<>();
        World world = region.corner1().getWorld();
        if (world == null) {
            throw new IllegalStateException("missing-world");
        }

        int minX = Math.min(region.corner1().getBlockX(), region.corner2().getBlockX());
        int maxX = Math.max(region.corner1().getBlockX(), region.corner2().getBlockX());
        int minY = Math.min(region.corner1().getBlockY(), region.corner2().getBlockY());
        int maxY = Math.max(region.corner1().getBlockY(), region.corner2().getBlockY());
        int minZ = Math.min(region.corner1().getBlockZ(), region.corner2().getBlockZ());
        int maxZ = Math.max(region.corner1().getBlockZ(), region.corner2().getBlockZ());

        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    Block block = world.getBlockAt(x, y, z);
                    blocks.add(new BlockSnapshot(x, y, z, block.getType().name(), block.getBlockData().getAsString()));
                }
            }
        }

        File snapshotFile = this.getSnapshotFile(arena);
        if (!snapshotFile.getParentFile().exists()) {
            snapshotFile.getParentFile().mkdirs();
        }

        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(snapshotFile)))) {
            out.writeUTF(world.getName());
            out.writeInt(blocks.size());
            for (BlockSnapshot snapshot : blocks) {
                out.writeInt(snapshot.x());
                out.writeInt(snapshot.y());
                out.writeInt(snapshot.z());
                out.writeUTF(snapshot.material());
                out.writeUTF(snapshot.blockData());
            }
        }
    }

    private @Nullable ArenaRegion loadRegion(@NotNull String arena) {
        Location one = this.firstCorners.get(arena);
        Location two = this.secondCorners.get(arena);
        if (one == null || two == null) {
            this.loadCornersFromConfig();
            one = this.firstCorners.get(arena);
            two = this.secondCorners.get(arena);
        }
        if (one == null || two == null) {
            return null;
        }
        if (one == null || two == null || one.getWorld() == null || two.getWorld() == null) {
            return null;
        }

        return new ArenaRegion(one, two);
    }

    private @Nullable Location fromSimpleCsv(@NotNull String raw) {
        String[] parts = raw.split(",");
        if (parts.length != 4) {
            return null;
        }

        World world = Bukkit.getWorld(parts[0]);
        if (world == null) {
            return null;
        }

        try {
            return new Location(world, Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private @NotNull String toSimpleCsv(@NotNull Location location) {
        World world = location.getWorld();
        return (world == null ? "world" : world.getName()) + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    private SnapshotData readSnapshot(@NotNull File snapshotFile) throws IOException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(snapshotFile)))) {
            String worldName = in.readUTF();
            int count = in.readInt();
            ArrayList<BlockSnapshot> blocks = new ArrayList<>(count);
            for (int i = 0; i < count; ++i) {
                blocks.add(new BlockSnapshot(in.readInt(), in.readInt(), in.readInt(), in.readUTF(), in.readUTF()));
            }
            return new SnapshotData(worldName, blocks);
        }
    }

    private void applySnapshot(@NotNull String arena, @NotNull SnapshotData data, @Nullable Runnable onComplete) {
        World world = Bukkit.getWorld(data.worldName());
        if (world == null) {
            this.running.remove(arena);
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        this.clearArenaEntities(world, data.blocks());
        new BukkitRunnable() {
            private int index;

            @Override
            public void run() {
                int applied = 0;
                while (this.index < data.blocks().size() && applied < APPLY_BATCH_SIZE) {
                    BlockSnapshot snapshot = data.blocks().get(this.index++);
                    Block block = world.getBlockAt(snapshot.x(), snapshot.y(), snapshot.z());
                    Material material = Material.matchMaterial(snapshot.material());
                    if (material == null) {
                        material = Material.AIR;
                    }
                    block.setType(material, false);
                    try {
                        block.setBlockData(Bukkit.createBlockData(snapshot.blockData()), false);
                    } catch (IllegalArgumentException ignored) {
                    }
                    applied++;
                }

                if (this.index >= data.blocks().size()) {
                    cancel();
                    ArenaRegenManager.this.running.remove(arena);
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 1L, 1L);
    }

    private void clearArenaEntities(@NotNull World world, @NotNull List<BlockSnapshot> blocks) {
        if (blocks.isEmpty()) {
            return;
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (BlockSnapshot snapshot : blocks) {
            minX = Math.min(minX, snapshot.x());
            minY = Math.min(minY, snapshot.y());
            minZ = Math.min(minZ, snapshot.z());
            maxX = Math.max(maxX, snapshot.x());
            maxY = Math.max(maxY, snapshot.y());
            maxZ = Math.max(maxZ, snapshot.z());
        }

        for (Entity entity : world.getEntities()) {
            Location location = entity.getLocation();
            if (location.getBlockX() >= minX && location.getBlockX() <= maxX
                && location.getBlockY() >= minY && location.getBlockY() <= maxY
                && location.getBlockZ() >= minZ && location.getBlockZ() <= maxZ
                && (entity instanceof Item || entity instanceof EnderCrystal)) {
                entity.remove();
            }
        }
    }

    private @NotNull File getSnapshotFile(@NotNull String arena) {
        File folder = new File(Main.getInstance().getDataFolder(), "arena-snapshots");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        return new File(folder, arena + ".bin");
    }

    private @NotNull File getConfigFile() {
        File folder = Main.getInstance().getDataFolder();
        if (!folder.exists()) {
            folder.mkdirs();
        }

        return new File(folder, "config.yml");
    }

    private void saveConfigAsync(@NotNull Consumer<YamlConfiguration> update) {
        File file = this.getConfigFile();
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            synchronized (this.fileLock) {
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                update.accept(yml);
                try {
                    yml.save(file);
                } catch (Exception ignored) {
                }
            }
        });
    }

    private void loadCornersFromConfig() {
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(this.getConfigFile());
        if (yml.getConfigurationSection("arenas") == null) {
            return;
        }

        for (String arena : yml.getConfigurationSection("arenas").getKeys(false)) {
            String corner1 = yml.getString("arenas." + arena + ".region.corner1");
            String corner2 = yml.getString("arenas." + arena + ".region.corner2");
            Location one = corner1 == null ? null : this.fromSimpleCsv(corner1);
            Location two = corner2 == null ? null : this.fromSimpleCsv(corner2);
            if (one != null) {
                this.firstCorners.put(arena, one);
            }
            if (two != null) {
                this.secondCorners.put(arena, two);
            }
        }
    }

    private record ArenaRegion(@NotNull Location corner1, @NotNull Location corner2) {}

    private record BlockSnapshot(int x, int y, int z, @NotNull String material, @NotNull String blockData) {}

    private record SnapshotData(@NotNull String worldName, @NotNull List<BlockSnapshot> blocks) {}
}
