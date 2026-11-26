package io.github.hello09x.fakeplayer.core.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PlayerListener implements Listener {
   private final FakeplayerManager manager;

   @Inject
   public PlayerListener(FakeplayerManager manager) {
      this.manager = manager;
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.MONITOR
   )
   public void onSneak(@NotNull PlayerToggleSneakEvent event) {
      Player player = event.getPlayer();
      List<Entity> passengers = player.getPassengers();
      if (!passengers.isEmpty()) {
         for (Entity passenger : passengers) {
            if (passenger instanceof Player target && this.manager.isFake(target)) {
               player.removePassenger(target);
            }
         }
      }
   }
}
