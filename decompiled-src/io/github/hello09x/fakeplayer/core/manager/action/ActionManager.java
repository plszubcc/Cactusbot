package io.github.hello09x.fakeplayer.core.manager.action;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.api.spi.ActionSetting;
import io.github.hello09x.fakeplayer.api.spi.ActionTicker;
import io.github.hello09x.fakeplayer.api.spi.ActionType;
import io.github.hello09x.fakeplayer.api.spi.NMSBridge;
import io.github.hello09x.fakeplayer.core.Main;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

@Singleton
public class ActionManager {

    private static final Logger log = Main.getInstance().getLogger();
    private final Map<UUID, Map<ActionType, ActionTicker>> managers = new HashMap();
    private final NMSBridge bridge;

    @Inject
    public ActionManager(NMSBridge bridge) {
        super();
        this.bridge = bridge;
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), this::tick, 0L, 1L);
    }

    public boolean hasActiveAction(@NotNull Player player, @NotNull ActionType action) {
        return Optional.ofNullable((Map) this.managers.get(player.getUniqueId())).map((managerx) -> {
            return (ActionTicker) managerx.get(action);
        }).filter((acx) -> {
            return acx.getSetting().remains > 0;
        }).isPresent();
    }

    public @NotNull @Unmodifiable Set<ActionType> getActiveActions(@NotNull Player player) {
        Map<ActionType, ActionTicker> manager = (Map) this.managers.get(player.getUniqueId());

        return manager != null && !this.managers.isEmpty() ? (Set) manager.entrySet().stream().filter((actionx) -> {
            int remains = ((ActionTicker) actionx.getValue()).getSetting().remains;

            return remains > 0 || remains == -1;
        }).map(Entry::getKey).collect(Collectors.toSet()) : Collections.emptySet();
    }

    public void setAction(@NotNull Player player, @NotNull ActionType action, @NotNull ActionSetting setting) {
        Map<ActionType, ActionTicker> managers = (Map) this.managers.computeIfAbsent(player.getUniqueId(), (keyx) -> {
            return new HashMap();
        });

        managers.put(action, this.bridge.createAction(player, action, setting));
    }

    public void stop(@NotNull Player player) {
        Map<ActionType, ActionTicker> managers = (Map) this.managers.get(player.getUniqueId());

        if (managers != null && !managers.isEmpty()) {
            for (Map.Entry<ActionType, ActionTicker> entry : managers.entrySet()) {
                if (!((ActionTicker) entry.getValue()).equals(ActionSetting.stop())) {
                    entry.setValue(this.bridge.createAction(player, (ActionType) entry.getKey(), ActionSetting.stop()));
                }
            }

        }
    }

    public void tick() {
        Iterator<Map.Entry<UUID, Map<ActionType, ActionTicker>>> itr = this.managers.entrySet().iterator();

        while (itr.hasNext()) {
            Map.Entry<UUID, Map<ActionType, ActionTicker>> entry = (Entry) itr.next();
            Player player = Bukkit.getPlayer((UUID) entry.getKey());

            if (player != null && player.isValid()) {
                ((Map) entry.getValue()).values().removeIf((tickerx) -> {
                    try {
                        return tickerx.tick();
                    } catch (Throwable throwable) {
                        ActionManager.log.warning(Throwables.getStackTraceAsString(throwable));
                        return false;
                    }
                });
                if (((Map) entry.getValue()).isEmpty()) {
                    itr.remove();
                }
            } else {
                itr.remove();

                for (ActionTicker ticker : ((Map) entry.getValue()).values()) {
                    ticker.stop();
                }
            }
        }

    }
}
