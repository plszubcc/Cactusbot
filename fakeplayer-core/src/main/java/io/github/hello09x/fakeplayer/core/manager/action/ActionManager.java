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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

@Singleton
public class ActionManager {
   private static final Logger log = Main.getInstance().getLogger();
   private final Map<UUID, Map<ActionType, ActionTicker>> managers = new HashMap<>();
   private final NMSBridge bridge;

   @Inject
   public ActionManager(NMSBridge bridge) {
      this.bridge = bridge;
      Bukkit.getScheduler().runTaskTimer(Main.getInstance(), this::tick, 0L, 1L);
   }

   public boolean hasActiveAction(@NotNull Player player, @NotNull ActionType action) {
      return Optional.ofNullable(this.managers.get(player.getUniqueId()))
         .map(manager -> manager.get(action))
         .filter(ac -> ac.getSetting().remains > 0)
         .isPresent();
   }

   @NotNull
   @Unmodifiable
   public Set<ActionType> getActiveActions(@NotNull Player player) {
      Map<ActionType, ActionTicker> manager = this.managers.get(player.getUniqueId());
      return manager != null && !this.managers.isEmpty() ? manager.entrySet().stream().filter(action -> {
         int remains = action.getValue().getSetting().remains;
         return remains > 0 || remains == -1;
      }).map(Entry::getKey).collect(Collectors.toSet()) : Collections.emptySet();
   }

   public void setAction(@NotNull Player player, @NotNull ActionType action, @NotNull ActionSetting setting) {
      Map<ActionType, ActionTicker> managers = this.managers.computeIfAbsent(player.getUniqueId(), key -> new HashMap<>());
      managers.put(action, this.bridge.createAction(player, action, setting));
   }

   public void stop(@NotNull Player player) {
      Map<ActionType, ActionTicker> managers = this.managers.get(player.getUniqueId());
      if (managers != null && !managers.isEmpty()) {
         for (Entry<ActionType, ActionTicker> entry : managers.entrySet()) {
            if (!entry.getValue().equals(ActionSetting.stop())) {
               entry.setValue(this.bridge.createAction(player, entry.getKey(), ActionSetting.stop()));
            }
         }
      }
   }

   public void tick() {
      Iterator<Entry<UUID, Map<ActionType, ActionTicker>>> itr = this.managers.entrySet().iterator();

      while (itr.hasNext()) {
         Entry<UUID, Map<ActionType, ActionTicker>> entry = itr.next();
         Player player = Bukkit.getPlayer(entry.getKey());
         if (player != null && player.isValid()) {
            entry.getValue().values().removeIf(tickerx -> {
               try {
                  return tickerx.tick();
               } catch (Throwable var2x) {
                  log.warning(Throwables.getStackTraceAsString(var2x));
                  return false;
               }
            });
            if (entry.getValue().isEmpty()) {
               itr.remove();
            }
         } else {
            itr.remove();

            for (ActionTicker ticker : entry.getValue().values()) {
               ticker.stop();
            }
         }
      }
   }
}
