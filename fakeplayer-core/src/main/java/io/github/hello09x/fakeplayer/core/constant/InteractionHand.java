package io.github.hello09x.fakeplayer.core.constant;

import lombok.Generated;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public enum InteractionHand {

    MAIN_HAND(EquipmentSlot.HAND), OFF_HAND(EquipmentSlot.OFF_HAND);

    final EquipmentSlot slot;

    public @NotNull EquipmentSlot asSlot() {
        return this.slot;
    }

    @Generated
    private InteractionHand(EquipmentSlot slot) {
        this.slot = slot;
    }
}
