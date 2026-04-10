package io.github.hello09x.fakeplayer.core.manager.pvp;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.config.GamemodeDefinition;
import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class QueueManager {

    private final FakeplayerConfig config;
    private final ArenaManager arenaManager;
    private final Map<String, Deque<UUID>> queues = new ConcurrentHashMap();
    private BukkitTask ticker;

    @Inject
    public QueueManager(FakeplayerConfig config, ArenaManager arenaManager) {
        this.config = config;
        this.arenaManager = arenaManager;
        this.loadFromConfig();
        this.startTicker();
    }

    public QueueJoinResult queue(@NotNull Player player, @NotNull String gamemode) {
        GamemodeDefinition definition = this.config.getGamemodes().get(gamemode);
        if (definition == null) {
            return QueueJoinResult.INVALID_GAMEMODE;
        }

        if (this.arenaManager.getArenaOf(player) != null) {
            return QueueJoinResult.ALREADY_QUEUED;
        }

        String currentQueue = this.findQueueOf(player.getUniqueId());
        if (currentQueue != null) {
            if (currentQueue.equalsIgnoreCase(gamemode)) {
                return QueueJoinResult.ALREADY_QUEUED;
            }

            this.removeFromQueue(player.getUniqueId(), currentQueue);
        }

        if (!this.hasConfiguredArena(definition)) {
            return QueueJoinResult.NO_ARENAS_CONFIGURED;
        }

        boolean hadFreeArena = this.arenaManager.findFreeArena(definition.arenas()) != null;
        Deque<UUID> queue = this.queues.computeIfAbsent(gamemode, key -> new ArrayDeque<>());
        queue.addLast(player.getUniqueId());
        this.persistQueue(gamemode);
        this.tickGamemode(gamemode);
        if (this.arenaManager.getArenaOf(player) != null) {
            return QueueJoinResult.JOINED;
        }

        return QueueJoinResult.QUEUED_WAITING;
    }

    public QueueLeaveResult leave(@NotNull Player player) {
        String arena = this.arenaManager.getArenaOf(player);
        if (arena != null) {
            this.arenaManager.endMatchOf(player);
            return QueueLeaveResult.LEFT_MATCH;
        }

        String queueName = this.findQueueOf(player.getUniqueId());
        if (queueName == null) {
            return QueueLeaveResult.NOT_QUEUED;
        }

        this.removeFromQueue(player.getUniqueId(), queueName);
        return QueueLeaveResult.LEFT_QUEUE;
    }

    public QueueLeaveResult leave(@NotNull Player player, @NotNull String gamemode) {
        String arena = this.arenaManager.getArenaOf(player);
        if (arena != null) {
            this.arenaManager.endMatchOf(player);
            return QueueLeaveResult.LEFT_MATCH;
        }

        Deque<UUID> queue = this.queues.get(gamemode);
        if (queue == null || !queue.remove(player.getUniqueId())) {
            return QueueLeaveResult.NOT_QUEUED;
        }

        this.persistQueue(gamemode);
        return QueueLeaveResult.LEFT_QUEUE;
    }

    public @NotNull List<String> queuedPlayers(@NotNull String gamemode) {
        Deque<UUID> queue = this.queues.get(gamemode);
        if (queue == null || queue.isEmpty()) {
            return List.of();
        }

        ArrayList<String> names = new ArrayList<>();
        for (UUID id : queue) {
            Player player = Bukkit.getPlayer(id);
            if (player != null) {
                names.add(player.getName());
            }
        }

        return List.copyOf(names);
    }

    private void startTicker() {
        if (this.ticker != null) {
            this.ticker.cancel();
        }

        this.ticker = new BukkitRunnable() {
            @Override
            public void run() {
                for (String gamemode : QueueManager.this.config.getGamemodes().keySet()) {
                    QueueManager.this.tickGamemode(gamemode);
                }
            }
        }.runTaskTimer(Main.getInstance(), 20L, 20L);
    }

    private void tickGamemode(@NotNull String gamemode) {
        GamemodeDefinition definition = this.config.getGamemodes().get(gamemode);
        if (definition == null) {
            return;
        }

        Deque<UUID> queue = this.queues.get(gamemode);
        if (queue == null || queue.isEmpty()) {
            return;
        }

        String freeArena = this.arenaManager.findFreeArena(definition.arenas());
        if (freeArena == null) {
            return;
        }

        UUID next;
        while ((next = queue.pollFirst()) != null) {
            Player player = Bukkit.getPlayer(next);
            if (player == null || !player.isOnline()) {
                continue;
            }

            this.persistQueue(gamemode);
            if (this.arenaManager.startMatch(player, freeArena)) {
                player.sendMessage(Component.text("Match found in arena " + freeArena + ".", NamedTextColor.GREEN));
                return;
            }

            freeArena = this.arenaManager.findFreeArena(definition.arenas());
            if (freeArena == null) {
                queue.addFirst(next);
                this.persistQueue(gamemode);
                return;
            }
        }

        this.persistQueue(gamemode);
    }

    private boolean hasConfiguredArena(@NotNull GamemodeDefinition definition) {
        if (definition.arenas().isEmpty()) {
            return false;
        }

        for (String arena : definition.arenas()) {
            if (this.config.getPvpArenas().containsKey(arena)) {
                return true;
            }
        }

        return false;
    }

    private @Nullable String findQueueOf(@NotNull UUID playerId) {
        for (Map.Entry<String, Deque<UUID>> entry : this.queues.entrySet()) {
            if (entry.getValue().contains(playerId)) {
                return entry.getKey();
            }
        }

        return null;
    }

    private void removeFromQueue(@NotNull UUID playerId, @NotNull String gamemode) {
        Deque<UUID> queue = this.queues.get(gamemode);
        if (queue == null) {
            return;
        }

        if (queue.remove(playerId)) {
            this.persistQueue(gamemode);
        }
    }

    private void loadFromConfig() {
        File file = this.getQueueFile();
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yml.getConfigurationSection("queues");
        if (section == null) {
            return;
        }

        for (String gamemode : section.getKeys(false)) {
            ArrayDeque<UUID> queue = new ArrayDeque<>();
            for (String raw : yml.getStringList("queues." + gamemode)) {
                try {
                    queue.add(UUID.fromString(raw));
                } catch (IllegalArgumentException ignored) {
                }
            }
            if (!queue.isEmpty()) {
                this.queues.put(gamemode, queue);
            }
        }
    }

    private void persistQueue(@NotNull String gamemode) {
        File file = this.getQueueFile();
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        if (yml.getConfigurationSection("queues") == null) {
            yml.createSection("queues");
        }

        ArrayList<String> values = new ArrayList<>();
        Deque<UUID> queue = this.queues.get(gamemode);
        if (queue != null) {
            for (UUID id : queue) {
                values.add(id.toString());
            }
        }

        yml.set("queues." + gamemode, values);
        try {
            yml.save(file);
        } catch (Exception ignored) {
        }
    }

    private @NotNull File getQueueFile() {
        File folder = Main.getInstance().getDataFolder();
        if (!folder.exists()) {
            folder.mkdirs();
        }

        return new File(folder, "queues.yml");
    }

    public static void playQueueFullFeedback(@NotNull Player player) {
        player.sendMessage(Component.text("All arenas are full right now. You were added to the queue.", NamedTextColor.YELLOW));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.9F, 0.8F);
    }

    public static void playNoArenaFeedback(@NotNull Player player) {
        player.sendMessage(Component.text("This gamemode has no arenas configured.", NamedTextColor.RED));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.9F, 0.6F);
    }

    public static void playJoinFeedback(@NotNull Player player, @NotNull String gamemode) {
        player.sendMessage(Component.text("You joined " + gamemode + " queue.", NamedTextColor.GRAY));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.9F, 1.2F);
    }

    public static void playLeaveFeedback(@NotNull Player player) {
        player.sendMessage(Component.text("You left the queue.", NamedTextColor.GRAY));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 0.8F, 1.0F);
    }

    public enum QueueJoinResult {
        JOINED,
        QUEUED_WAITING,
        ALREADY_QUEUED,
        INVALID_GAMEMODE,
        NO_ARENAS_CONFIGURED
    }

    public enum QueueLeaveResult {
        LEFT_QUEUE,
        LEFT_MATCH,
        NOT_QUEUED
    }
}
