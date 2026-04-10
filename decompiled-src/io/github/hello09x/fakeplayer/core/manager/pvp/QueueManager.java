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
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

@Singleton
public class QueueManager {

    private final FakeplayerConfig config;
    private final ArenaManager arenaManager;
    private final Map<String, Deque<UUID>> queues = new ConcurrentHashMap();

    @Inject
    public QueueManager(FakeplayerConfig config, ArenaManager arenaManager) {
        super();
        this.config = config;
        this.arenaManager = arenaManager;
        this.loadFromConfig();
        this.startTicker();
    }

    public QueueManager.@NotNull QueueJoinResult queue(@NotNull Player player, @NotNull String gamemode) {
        GamemodeDefinition gm = (GamemodeDefinition) this.config.getGamemodes().get(gamemode);

        if (gm == null) {
            return QueueManager.QueueJoinResult.INVALID_GAMEMODE;
        } else {
            Deque<UUID> q = (Deque) this.queues.computeIfAbsent(gamemode, (kx) -> {
                return new ArrayDeque();
            });

            if (q.contains(player.getUniqueId())) {
                return QueueManager.QueueJoinResult.ALREADY_QUEUED;
            } else {
                q.addLast(player.getUniqueId());
                this.persistQueue(gamemode);
                this.tickGamemode(gamemode);
                return QueueManager.QueueJoinResult.JOINED;
            }
        }
    }

    public QueueManager.@NotNull QueueLeaveResult leave(@NotNull Player player) {
        if (this.arenaManager.getArenaOf(player) != null) {
            this.arenaManager.endMatchOf(player);
            return QueueManager.QueueLeaveResult.LEFT_MATCH;
        } else {
            boolean removed = false;

            for (Map.Entry<String, Deque<UUID>> entry : this.queues.entrySet()) {
                if (((Deque) entry.getValue()).remove(player.getUniqueId())) {
                    this.persistQueue((String) entry.getKey());
                    removed = true;
                }
            }

            if (removed) {
                this.arenaManager.sendPlayerToLobby(player);
                return QueueManager.QueueLeaveResult.LEFT_QUEUE;
            } else {
                return QueueManager.QueueLeaveResult.NOT_QUEUED;
            }
        }
    }

    public QueueManager.@NotNull QueueLeaveResult leave(@NotNull Player player, @NotNull String gamemode) {
        if (this.arenaManager.getArenaOf(player) != null) {
            this.arenaManager.endMatchOf(player);
            return QueueManager.QueueLeaveResult.LEFT_MATCH;
        } else {
            Deque<UUID> q = (Deque) this.queues.get(gamemode);

            if (q != null && q.remove(player.getUniqueId())) {
                this.persistQueue(gamemode);
                this.arenaManager.sendPlayerToLobby(player);
                return QueueManager.QueueLeaveResult.LEFT_QUEUE;
            } else {
                return QueueManager.QueueLeaveResult.NOT_QUEUED;
            }
        }
    }

    private void startTicker() {
        (new BukkitRunnable() {
            public void run() {
                for (String gm : QueueManager.this.config.getGamemodes().keySet()) {
                    QueueManager.this.tickGamemode(gm);
                }

            }
        }).runTaskTimer(Main.getInstance(), 20L, 20L);
    }

    private void tickGamemode(@NotNull String gamemode) {
        GamemodeDefinition gm = (GamemodeDefinition) this.config.getGamemodes().get(gamemode);

        if (gm != null) {
            Deque<UUID> q = (Deque) this.queues.get(gamemode);

            if (q != null && !q.isEmpty()) {
                String free = this.arenaManager.findFreeArena(gm.arenas());

                if (free != null) {
                    UUID next;

                    while ((next = (UUID) q.pollFirst()) != null) {
                        Player player = Bukkit.getPlayer(next);

                        if (player != null && player.isOnline()) {
                            this.persistQueue(gamemode);
                            this.arenaManager.startMatch(player, free);
                            return;
                        }
                    }

                    this.persistQueue(gamemode);
                }
            }
        }
    }

    private void loadFromConfig() {
        File file = this.getConfigFile();
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yml.getConfigurationSection("queues");

        if (section != null) {
            for (String gm : section.getKeys(false)) {
                List<String> list = yml.getStringList("queues." + gm);
                ArrayDeque<UUID> q = new ArrayDeque();

                for (String s : list) {
                    try {
                        q.add(UUID.fromString(s));
                    } catch (Exception exception) {
                        ;
                    }
                }

                if (!q.isEmpty()) {
                    this.queues.put(gm, q);
                }
            }

        }
    }

    private void persistQueue(@NotNull String gamemode) {
        File file = this.getConfigFile();
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

        if (yml.getConfigurationSection("queues") == null) {
            yml.createSection("queues");
        }

        Deque<UUID> q = (Deque) this.queues.get(gamemode);
        ArrayList<String> list = new ArrayList();

        if (q != null) {
            for (UUID id : q) {
                list.add(id.toString());
            }
        }

        yml.set("queues." + gamemode, list);

        try {
            yml.save(file);
        } catch (Exception exception) {
            ;
        }

        this.config.reload();
    }

    private @NotNull File getConfigFile() {
        Main plugin = Main.getInstance();
        File folder = plugin.getDataFolder();

        if (!folder.exists()) {
            folder.mkdirs();
        }

        return new File(folder, "config.yml");
    }

    public static enum QueueJoinResult {

        JOINED, ALREADY_QUEUED, INVALID_GAMEMODE;

        private QueueJoinResult() {}
    }

    public static enum QueueLeaveResult {

        LEFT_QUEUE, LEFT_MATCH, NOT_QUEUED;

        private QueueLeaveResult() {}
    }
}
