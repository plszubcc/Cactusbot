package io.github.hello09x.fakeplayer.core.manager.combat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PresetHitTracker implements Listener {

    private final FakeplayerManager fakeplayerManager;
    private final Map<UUID, PresetHitTracker.HitRecord> lastHits = new ConcurrentHashMap();

    @Inject
    public PresetHitTracker(FakeplayerManager fakeplayerManager) {
        super();
        this.fakeplayerManager = fakeplayerManager;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamage(@NotNull EntityDamageByEntityEvent event) {
        Entity target = event.getDamager();

        if (target instanceof Player bot) {
            if (!this.fakeplayerManager.isNotFake(bot)) {
                target = event.getEntity();
                this.lastHits.put(bot.getUniqueId(), new PresetHitTracker.HitRecord(target.getUniqueId(), System.currentTimeMillis()));
                return;
            }
        }

    }

    public boolean targetHitRecently(@NotNull Player bot, @NotNull Entity target, int withinTicks) {
        PresetHitTracker.HitRecord record = (PresetHitTracker.HitRecord) this.lastHits.get(bot.getUniqueId());

        return record == null ? false : (!record.targetId().equals(target.getUniqueId()) ? false : System.currentTimeMillis() - record.atMillis() <= (long) withinTicks * 50L);
    }

    public void clear(@NotNull Player bot) {
        this.lastHits.remove(bot.getUniqueId());
    }

    private static record HitRecord(UUID targetId, long atMillis) {

        private HitRecord {
            super();
        }
    }
}
