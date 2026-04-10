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
        super();
    }

    public static @Nullable String encode(@Nullable PresetInventorySnapshot snapshot) {
        if (snapshot == null) {
            return null;
        } else {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                try (BukkitObjectOutputStream oos = new BukkitObjectOutputStream(baos)) {
                    oos.writeObject(snapshot.storageContents());
                    oos.writeObject(snapshot.armorContents());
                    oos.writeObject(snapshot.offhand());
                    oos.writeInt(snapshot.heldSlot());
                }

                return Base64.getEncoder().encodeToString(baos.toByteArray());
            } catch (Exception exception) {
                return null;
            }
        }
    }

    public static @Nullable PresetInventorySnapshot decode(@Nullable String base64) {
        if (base64 != null && !base64.isBlank()) {
            try {
                byte[] bytes = Base64.getDecoder().decode(base64);

                PresetInventorySnapshot presetinventorysnapshot;

                try (BukkitObjectInputStream ois = new BukkitObjectInputStream(new ByteArrayInputStream(bytes))) {
                    ItemStack[] storage = (ItemStack[]) ois.readObject();
                    ItemStack[] armor = (ItemStack[]) ois.readObject();
                    ItemStack offhand = (ItemStack) ois.readObject();
                    int heldSlot = ois.readInt();

                    presetinventorysnapshot = new PresetInventorySnapshot(storage == null ? new ItemStack[0] : storage, armor == null ? new ItemStack[0] : armor, offhand, heldSlot);
                }

                return presetinventorysnapshot;
            } catch (Exception exception) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static @NotNull PresetInventorySnapshot capture(@NotNull Player player) {
        PlayerInventory inv = player.getInventory();

        return new PresetInventorySnapshot((ItemStack[]) inv.getStorageContents().clone(), (ItemStack[]) inv.getArmorContents().clone(), inv.getItemInOffHand(), inv.getHeldItemSlot());
    }
}
