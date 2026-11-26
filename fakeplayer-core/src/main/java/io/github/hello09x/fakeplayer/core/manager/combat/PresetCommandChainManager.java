package io.github.hello09x.fakeplayer.core.manager.combat;

import com.google.inject.Singleton;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class PresetCommandChainManager {
   private final Map<UUID, Map<String, Long>> executions = new ConcurrentHashMap<>();

   public PresetCommandChainManager() {
   }

   public void markExecuted(@NotNull Player bot, @Nullable String id) {
      if (id != null && !id.isBlank()) {
         this.executions.computeIfAbsent(bot.getUniqueId(), ignored -> new ConcurrentHashMap<>()).put(this.normalize(id), System.currentTimeMillis());
      }
   }

   public boolean wasExecutedRecently(@NotNull Player bot, @Nullable String id, @Nullable Integer withinTicks) {
      if (id != null && !id.isBlank()) {
         Map<String, Long> byBot = this.executions.get(bot.getUniqueId());
         if (byBot == null) {
            return false;
         } else {
            Long timestamp = byBot.get(this.normalize(id));
            if (timestamp == null) {
               return false;
            } else {
               return withinTicks != null && withinTicks > 0 ? System.currentTimeMillis() - timestamp <= withinTicks.intValue() * 50L : true;
            }
         }
      } else {
         return true;
      }
   }

   public void clear(@NotNull Player bot) {
      this.executions.remove(bot.getUniqueId());
   }

   @NotNull
   private String normalize(@NotNull String value) {
      return value.trim().toLowerCase(Locale.ROOT);
   }
}
