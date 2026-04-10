package io.github.hello09x.fakeplayer.core.manager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.BotSoundStep;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

@Singleton
public class BotSoundManager implements Listener {

    private final FakeplayerManager fakeplayerManager;
    private final FakeplayerConfig config;
    private final Map<UUID, Location> lastLocations = new HashMap();
    private final Map<UUID, Integer> moveIndices = new HashMap();

    @Inject
    public BotSoundManager(FakeplayerManager fakeplayerManager, FakeplayerConfig config) {
        super();
        this.fakeplayerManager = fakeplayerManager;
        this.config = config;
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), this::tickMoveSounds, (long) config.getBotMoveIntervalTicks(), (long) config.getBotMoveIntervalTicks());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (this.config.isBotSoundsEnabled() && this.fakeplayerManager.isFake(player)) {
            this.lastLocations.put(player.getUniqueId(), player.getLocation().clone());
            this.playSequence(player, this.config.getBotSpawnSounds());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onHit(@NotNull EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player player) {
            if (this.config.isBotSoundsEnabled() && this.fakeplayerManager.isFake(player)) {
                this.playSequence(player, this.config.getBotHitSounds());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getPlayer();

        if (this.config.isBotSoundsEnabled() && this.fakeplayerManager.isFake(player)) {
            this.playSequence(player, this.config.getBotDeathSounds());
            this.lastLocations.remove(player.getUniqueId());
            this.moveIndices.remove(player.getUniqueId());
        }
    }

    private void tickMoveSounds() {
        if (this.config.isBotSoundsEnabled() && !this.config.getBotMoveSounds().isEmpty()) {
            for (Player bot : this.fakeplayerManager.getAll()) {
                Location current = bot.getLocation();
                Location previous = (Location) this.lastLocations.put(bot.getUniqueId(), current.clone());

                if (previous != null && previous.getWorld().equals(current.getWorld()) && previous.distance(current) >= this.config.getBotMoveMinDistance()) {
                    List<BotSoundStep> steps = this.config.getBotMoveSounds();
                    Integer index = (Integer) this.moveIndices.getOrDefault(bot.getUniqueId(), 0);

                    this.playStep(bot, (BotSoundStep) steps.get(index % steps.size()));
                    this.moveIndices.put(bot.getUniqueId(), (index + 1) % steps.size());
                }
            }

        }
    }

    private void playSequence(@NotNull Player player, @NotNull List<BotSoundStep> steps) {
        for (BotSoundStep step : steps) {
            if (step.delayTicks() <= 0) {
                this.playStep(player, step);
            } else {
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    if (player.isOnline()) {
                        this.playStep(player, step);
                    }

                }, (long) step.delayTicks());
            }
        }

    }

    private void playStep(@NotNull Player player, @NotNull BotSoundStep step) {
        float volume = step.volume() * this.config.getBotSoundsMasterVolume();

        if (volume > 0.0F) {
            player.getWorld().playSound(player.getLocation(), step.sound(), volume, step.pitch());
        }
    }
}
