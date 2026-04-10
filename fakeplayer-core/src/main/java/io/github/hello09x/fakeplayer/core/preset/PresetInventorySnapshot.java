package io.github.hello09x.fakeplayer.core.preset;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record PresetInventorySnapshot(@NotNull ItemStack[] storageContents, @NotNull ItemStack[] armorContents, @Nullable ItemStack offhand, int heldSlot) {

    public PresetInventorySnapshot {
        super();
    }
}
