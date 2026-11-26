package io.github.hello09x.fakeplayer.core.preset;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PresetInventoryCodec {
   private PresetInventoryCodec() {
   }

   @Nullable
   public static String encode(@Nullable PresetInventorySnapshot snapshot) {
      if (snapshot == null) {
         return null;
      } else {
         try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BukkitObjectOutputStream oos = new BukkitObjectOutputStream(baos);

            try {
               oos.writeObject(snapshot.storageContents());
               oos.writeObject(snapshot.armorContents());
               oos.writeObject(snapshot.offhand());
               oos.writeInt(snapshot.heldSlot());
            } catch (Throwable var6) {
               try {
                  oos.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }

               throw var6;
            }

            oos.close();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
         } catch (Exception var7) {
            return null;
         }
      }
   }

   @Nullable
   public static PresetInventorySnapshot decode(@Nullable String base64) {
      if (base64 != null && !base64.isBlank()) {
         try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            BukkitObjectInputStream ois = new BukkitObjectInputStream(new ByteArrayInputStream(bytes));

            PresetInventorySnapshot var7;
            try {
               ItemStack[] storage = (ItemStack[])ois.readObject();
               ItemStack[] armor = (ItemStack[])ois.readObject();
               ItemStack offhand = (ItemStack)ois.readObject();
               int heldSlot = ois.readInt();
               var7 = new PresetInventorySnapshot(storage == null ? new ItemStack[0] : storage, armor == null ? new ItemStack[0] : armor, offhand, heldSlot);
            } catch (Throwable var9) {
               try {
                  ois.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }

               throw var9;
            }

            ois.close();
            return var7;
         } catch (Exception var10) {
            return null;
         }
      } else {
         return null;
      }
   }

   @NotNull
   public static PresetInventorySnapshot capture(@NotNull Player player) {
      PlayerInventory inv = player.getInventory();
      return new PresetInventorySnapshot(
         (ItemStack[])inv.getStorageContents().clone(), (ItemStack[])inv.getArmorContents().clone(), inv.getItemInOffHand(), inv.getHeldItemSlot()
      );
   }
}
