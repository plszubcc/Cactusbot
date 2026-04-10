package io.github.hello09x.fakeplayer.core.entity.action.impl;

import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.api.spi.NMSServerPlayer;
import io.papermc.paper.entity.LookAnchor;
import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LookAtEntityAction implements Action {

    private final @NotNull NMSServerPlayer player;

    public LookAtEntityAction(@NotNull NMSServerPlayer player) {
        super();
        this.player = player;
    }

    public boolean tick() {
        Player bukkitPlayer = this.player.getPlayer();
        Collection<Damageable> entities = bukkitPlayer.getLocation().getNearbyEntitiesByType(Damageable.class, 4.5D, 4.5D, 4.5D);

        if (entities.isEmpty()) {
            return false;
        } else {
            Entity entity = this.getNearestEntity(bukkitPlayer, entities);

            if (entity == null) {
                return false;
            } else {
                bukkitPlayer.lookAt(entity, LookAnchor.EYES, LookAnchor.EYES);
                this.player.resetLastActionTime();
                return true;
            }
        }
    }

    public void inactiveTick() {}

    public void stop() {}

    private @Nullable Entity getNearestEntity(@NotNull Player player, @NotNull Collection<? extends Entity> entities) {
        if (entities.isEmpty()) {
            return null;
        } else {
            Location loc = player.getLocation();
            Entity nearest = null;
            double distance = 0.0D;

            for (Entity entity : entities) {
                if (nearest == null) {
                    nearest = entity;
                } else {
                    double d = loc.distance(entity.getLocation());

                    if (d < distance) {
                        nearest = entity;
                        distance = d;
                    }
                }
            }

            return nearest;
        }
    }
}
