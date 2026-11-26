package io.github.hello09x.fakeplayer.core.manager;

import com.google.inject.Singleton;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class GlideEquipmentManager {
   private final Map<UUID, GlideEquipmentManager.GlideEquipmentState> states = new ConcurrentHashMap<>();

   public GlideEquipmentManager() {
   }

   public boolean equipElytraForGlide(@NotNull Player player) {
      PlayerInventory inventory = player.getInventory();
      ItemStack currentChest = inventory.getChestplate();
      if (this.isElytra(currentChest)) {
         return true;
      } else {
         int slot = this.findElytraSlot(inventory);
         if (slot < 0) {
            return false;
         } else {
            ItemStack elytra = inventory.getItem(slot);
            if (!this.isElytra(elytra)) {
               return false;
            } else {
               ItemStack previousChest = currentChest != null && !currentChest.getType().isAir() ? currentChest.clone() : null;
               inventory.setChestplate(elytra.clone());
               inventory.setItem(slot, previousChest);
               this.states.put(player.getUniqueId(), new GlideEquipmentManager.GlideEquipmentState(slot, previousChest));
               return true;
            }
         }
      }
   }

   public void restoreChestplate(@NotNull Player player) {
      GlideEquipmentManager.GlideEquipmentState state = this.states.remove(player.getUniqueId());
      if (state != null) {
         PlayerInventory inventory = player.getInventory();
         ItemStack equipped = inventory.getChestplate();
         ItemStack swappedSlotItem = inventory.getItem(state.slot());
         if (this.isElytra(equipped)) {
            inventory.setChestplate(state.previousChestplate());
            inventory.setItem(state.slot(), equipped.clone());
            if (state.previousChestplate() == null && swappedSlotItem != null && !swappedSlotItem.getType().isAir()) {
               ItemStack current = inventory.getItem(state.slot());
               if (current == null || current.getType().isAir()) {
                  inventory.setItem(state.slot(), swappedSlotItem);
               }
            }
         }
      }
   }

   private int findElytraSlot(@NotNull PlayerInventory inventory) {
      for (int slot = 0; slot < 36; slot++) {
         if (this.isElytra(inventory.getItem(slot))) {
            return slot;
         }
      }

      return -1;
   }

   private boolean isElytra(@Nullable ItemStack item) {
      return item != null && item.getType() == Material.ELYTRA && item.getAmount() > 0;
   }

   private record GlideEquipmentState(int slot, @Nullable ItemStack previousChestplate) {
   }
}
