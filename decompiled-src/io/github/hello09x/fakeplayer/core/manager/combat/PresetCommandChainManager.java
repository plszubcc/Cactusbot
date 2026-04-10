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

    private final Map<UUID, Map<String, Long>> executions = new ConcurrentHashMap();

    public PresetCommandChainManager() {
        super();
    }

    public void markExecuted(@NotNull Player bot, @Nullable String id) {
        if (id != null && !id.isBlank()) {
            ((Map) this.executions.computeIfAbsent(bot.getUniqueId(), (ignoredx) -> {
                return new ConcurrentHashMap();
            })).put(this.normalize(id), System.currentTimeMillis());
        }
    }

    public boolean wasExecutedRecently(@NotNull Player bot, @Nullable String id, @Nullable Integer withinTicks) {
        if (id != null && !id.isBlank()) {
            Map<String, Long> byBot = (Map) this.executions.get(bot.getUniqueId());

            if (byBot == null) {
                return false;
            } else {
                Long timestamp = (Long) byBot.get(this.normalize(id));

                return timestamp == null ? false : (withinTicks != null && withinTicks > 0 ? System.currentTimeMillis() - timestamp <= (long) withinTicks * 50L : true);
            }
        } else {
            return true;
        }
    }

    public void clear(@NotNull Player bot) {
        this.executions.remove(bot.getUniqueId());
    }

    private @NotNull String normalize(@NotNull String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
