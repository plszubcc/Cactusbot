package io.github.hello09x.fakeplayer.core.manager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.api.spi.ActionSetting;
import io.github.hello09x.fakeplayer.api.spi.ActionType;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.action.ActionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

@Singleton
public class FakeplayerAutofishManager implements Listener {
   private final FakeplayerManager manager;
   private final ActionManager actionManager;

   @Inject
   public FakeplayerAutofishManager(FakeplayerManager manager, ActionManager actionManager) {
      this.manager = manager;
      this.actionManager = actionManager;
   }

   public boolean isAutofish(@NotNull Player fake) {
      return fake.hasMetadata("fakeplayer:autofish");
   }

   public void setAutofish(@NotNull Player target, boolean autofish) {
      if (!autofish) {
         target.removeMetadata("fakeplayer:autofish", Main.getInstance());
      } else {
         target.setMetadata("fakeplayer:autofish", new FixedMetadataValue(Main.getInstance(), true));
      }
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.MONITOR
   )
   public void autoFishing(@NotNull PlayerFishEvent event) {
      if (event.getState() == State.BITE) {
         Player player = event.getPlayer();
         if (!this.manager.isNotFake(player)) {
            if (player.hasMetadata("fakeplayer:autofish")) {
               Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                  this.actionManager.setAction(player, ActionType.USE, ActionSetting.once());
                  Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> this.actionManager.setAction(player, ActionType.USE, ActionSetting.once()), 20L);
               }, 1L);
            }
         }
      }
   }
}
