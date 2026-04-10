package io.github.hello09x.fakeplayer.core.manager.combat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.core.manager.pvp.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class CombatTargetResolver {

    private static final double FALLBACK_RANGE = 24.0D;
    private final ArenaManager arenaManager;
    private final FakeplayerManager fakeplayerManager;

    @Inject
    public CombatTargetResolver(ArenaManager arenaManager, FakeplayerManager fakeplayerManager) {
        super();
        this.arenaManager = arenaManager;
        this.fakeplayerManager = fakeplayerManager;
    }

    public @Nullable Player resolve(@NotNull Player bot) {
        Player arenaOpponent = this.arenaManager.getOpponent(bot);

        if (arenaOpponent != null && arenaOpponent.isOnline()) {
            return arenaOpponent;
        } else {
            Player nearest = null;
            double nearestDistance = 1.7976931348623157E308D;
            Location origin = bot.getLocation();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.equals(bot) && !this.fakeplayerManager.isFake(player) && player.getWorld().equals(bot.getWorld())) {
                    double distance = origin.distance(player.getLocation());

                    if (distance <= 24.0D && distance < nearestDistance) {
                        nearest = player;
                        nearestDistance = distance;
                    }
                }
            }

            return nearest;
        }
    }

    public double distance(@NotNull Player bot, @NotNull Player target) {
        Location botLoc = bot.getLocation();
        Location targetLoc = target.getLocation();

        return !botLoc.getWorld().equals(targetLoc.getWorld()) ? 1.7976931348623157E308D : botLoc.distance(targetLoc);
    }
}
