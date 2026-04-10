package io.github.hello09x.fakeplayer.core.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.config.PresetCommandEntry;
import io.github.hello09x.fakeplayer.core.config.PresetDefinition;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.core.manager.combat.CombatTargetResolver;
import io.github.hello09x.fakeplayer.core.manager.combat.PresetCommandChainManager;
import io.github.hello09x.fakeplayer.core.manager.combat.PresetConditionEvaluator;
import io.github.hello09x.fakeplayer.core.manager.combat.PresetHitTracker;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class FakeplayerLifecycleListener implements Listener {

    private final FakeplayerManager manager;
    private final FakeplayerConfig config;
    private final PresetConditionEvaluator conditionEvaluator;
    private final CombatTargetResolver targetResolver;
    private final PresetCommandChainManager commandChainManager;
    private final PresetHitTracker hitTracker;
    private final Map<UUID, List<BukkitTask>> presetTasks = new ConcurrentHashMap();
    private final Map<UUID, String> pendingFakeQuits = new ConcurrentHashMap();

    @Inject
    public FakeplayerLifecycleListener(FakeplayerManager manager, FakeplayerConfig config, PresetConditionEvaluator conditionEvaluator, CombatTargetResolver targetResolver, PresetCommandChainManager commandChainManager, PresetHitTracker hitTracker) {
        super();
        this.manager = manager;
        this.config = config;
        this.conditionEvaluator = conditionEvaluator;
        this.targetResolver = targetResolver;
        this.commandChainManager = commandChainManager;
        this.hitTracker = hitTracker;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPostSpawn(@NotNull PlayerJoinEvent event) {
        this.runPostSpawnCommands(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onAfterSpawn(@NotNull PlayerJoinEvent event) {
        this.runAfterSpawnCommands(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPostQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!this.manager.isNotFake(player)) {
            this.stopPresetTasks(player.getUniqueId());
            this.commandChainManager.clear(player);
            this.hitTracker.clear(player);
            this.pendingFakeQuits.put(player.getUniqueId(), this.manager.getCreatorName(player));
            this.manager.dispatchCommands(player, this.config.getPostQuitCommands());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onAfterQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (this.pendingFakeQuits.containsKey(uuid)) {
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                try {
                    this.manager.dispatchCommands(new FakeplayerManager.DispatchCommandArgs(player.getName(), uuid.toString(), (String) this.pendingFakeQuits.get(uuid)), this.config.getAfterQuitCommands());
                } finally {
                    this.pendingFakeQuits.remove(uuid);
                }

            }, 1L);
        }
    }

    private void runPostSpawnCommands(@NotNull Player player) {
        if (!this.manager.isNotFake(player)) {
            this.manager.dispatchCommands(player, this.config.getPostSpawnCommands());
        }
    }

    private void runAfterSpawnCommands(@NotNull Player player) {
        if (!this.manager.isNotFake(player)) {
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                if (player.isOnline()) {
                    this.manager.dispatchCommands(player, this.config.getAfterSpawnCommands());
                    String presetName = this.getPresetName(player);

                    if (presetName != null) {
                        PresetDefinition preset = (PresetDefinition) this.config.getPresets().get(presetName);

                        if (preset != null) {
                            this.startPresetTasks(player, preset);
                        }
                    }

                    this.manager.issueCommands(player, this.config.getSelfCommands());
                }

            }, 20L);
        }
    }

    private void startPresetTasks(@NotNull Player player, @NotNull PresetDefinition preset) {
        this.stopPresetTasks(player.getUniqueId());
        ArrayList<BukkitTask> tasks = new ArrayList();

        for (PresetCommandEntry c : preset.commands()) {
            if (c.repeatTicks() <= 0 && !c.command().isBlank()) {
                int delay = Math.max(0, c.delayTicks());

                if (delay <= 0) {
                    this.dispatchPresetCommand(player, c);
                } else {
                    BukkitTask t = Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                        if (player.isOnline() && !this.manager.isNotFake(player) && player.hasMetadata("fakeplayer:preset")) {
                            this.dispatchPresetCommand(player, c);
                        }
                    }, (long) delay);

                    tasks.add(t);
                }
            }
        }

        for (PresetCommandEntry c : preset.commands()) {
            if (c.repeatTicks() > 0 && !c.command().isBlank()) {
                int interval = Math.max(1, c.repeatTicks());
                int initialDelay = c.delayTicks() > 0 ? c.delayTicks() : interval;
                long startedAt = System.currentTimeMillis();
                AtomicInteger executed = new AtomicInteger();
                BukkitTask task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
                    if (player.isOnline() && !this.manager.isNotFake(player) && player.hasMetadata("fakeplayer:preset")) {
                        if (c.stopTicks() > 0) {
                            long elapsedTicks = Math.max(0L, (System.currentTimeMillis() - startedAt) / 50L);

                            if (elapsedTicks >= (long) c.stopTicks()) {
                                return;
                            }
                        }

                        if (c.limit() <= 0 || executed.get() < c.limit()) {
                            if (this.dispatchPresetCommand(player, c)) {
                                executed.incrementAndGet();
                            }

                        }
                    } else {
                        this.stopPresetTasks(player.getUniqueId());
                    }
                }, (long) initialDelay, (long) interval);

                tasks.add(task);
            }
        }

        this.presetTasks.put(player.getUniqueId(), tasks);
    }

    private boolean dispatchPresetCommand(@NotNull Player bot, @NotNull PresetCommandEntry entry) {
        String command = entry.command();

        if (command.isBlank()) {
            return false;
        } else if (!this.conditionEvaluator.test(bot, entry.conditions())) {
            return false;
        } else {
            Player target = this.targetResolver.resolve(bot);
            String formatted = command;

            if (target != null) {
                formatted = command.replace("%target%", target.getName());
            }

            if (this.manager.dispatchCommand(bot, formatted)) {
                this.commandChainManager.markExecuted(bot, entry.id());
                return true;
            } else {
                return false;
            }
        }
    }

    private void stopPresetTasks(@NotNull UUID uuid) {
        List<BukkitTask> tasks = (List) this.presetTasks.remove(uuid);

        if (tasks != null) {
            for (BukkitTask task : tasks) {
                if (task != null && !task.isCancelled()) {
                    task.cancel();
                }
            }

        }
    }

    private @Nullable String getPresetName(@NotNull Player player) {
        return (String) player.getMetadata("fakeplayer:preset").stream().filter((vx) -> {
            return vx.getOwningPlugin() == Main.getInstance();
        }).findFirst().map((vx) -> {
            return vx.asString();
        }).filter((vx) -> {
            return !vx.isBlank();
        }).orElse((Object) null);
    }
}
