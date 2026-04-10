package io.github.hello09x.devtools.core.utils;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.jetbrains.annotations.NotNull;

public abstract class EntityUtils {

    public EntityUtils() {
        super();
    }

    public static boolean teleportAndSound(@NotNull Entity entity, @NotNull Location loc, TeleportCause cause) {
        if (entity.teleport(loc)) {
            loc.getWorld().playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            return true;
        } else {
            return false;
        }
    }

    public static boolean teleportAndSound(@NotNull Entity entity, @NotNull Location loc) {
        return teleportAndSound(entity, loc, TeleportCause.PLUGIN);
    }
}
