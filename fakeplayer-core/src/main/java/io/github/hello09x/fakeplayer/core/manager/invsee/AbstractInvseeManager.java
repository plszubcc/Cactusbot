package io.github.hello09x.fakeplayer.core.manager.invsee;

import io.github.hello09x.fakeplayer.core.entity.Fakeplayer;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerList;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractInvseeManager implements InvseeManager {
   protected final FakeplayerManager manager;
   protected final FakeplayerList fakeplayerList;

   protected AbstractInvseeManager(FakeplayerManager manager, FakeplayerList fakeplayerList) {
      this.manager = manager;
      this.fakeplayerList = fakeplayerList;
   }

   @Override
   public boolean invsee(@NotNull Player viewer, @NotNull Player whom) {
      Fakeplayer fp = this.fakeplayerList.getByUUID(whom.getUniqueId());
      if (fp == null) {
         return false;
      } else if (!viewer.isOp() && !fp.isCreatedBy(viewer)) {
         return false;
      } else {
         InventoryView view = this.openInventory(viewer, whom);
         if (view == null) {
            return false;
         } else {
            whom.getLocation().getWorld().playSound(whom.getLocation(), Sound.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.3F, 1.0F);
            return true;
         }
      }
   }

   @Nullable
   protected abstract InventoryView openInventory(@NotNull Player var1, @NotNull Player var2);

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.LOWEST
   )
   public void fixDragInventory(@NotNull InventoryDragEvent event) {
      Inventory top = event.getView().getTopInventory();
      if (top.getType() == InventoryType.PLAYER
         && top.getHolder() instanceof Player whom
         && this.manager.isFake(whom)
         && event.getNewItems().keySet().stream().anyMatch(slot -> slot > 35)) {
         event.setCancelled(true);
      }
   }
}
