package io.github.hello09x.fakeplayer.core.entity.action.impl;

import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.api.spi.NMSServerPlayer;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class DropInventoryAction implements Action {
   @NotNull
   private final NMSServerPlayer player;

   public DropInventoryAction(@NotNull NMSServerPlayer player) {
      this.player = player;
   }

   @Override
   public boolean tick() {
      PlayerInventory inventory = this.player.getPlayer().getInventory();

      for (int i = inventory.getSize(); i >= 0; i--) {
         this.player.drop(i, false, true);
      }

      this.player.resetLastActionTime();
      return true;
   }

   @Override
   public void inactiveTick() {
   }

   @Override
   public void stop() {
   }
}
