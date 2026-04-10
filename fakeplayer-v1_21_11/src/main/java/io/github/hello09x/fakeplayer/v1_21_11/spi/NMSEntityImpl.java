package io.github.hello09x.fakeplayer.v1_21_11.spi;

import io.github.hello09x.fakeplayer.api.spi.NMSEntity;
import lombok.Generated;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftEntity;
import org.jetbrains.annotations.NotNull;

public class NMSEntityImpl implements NMSEntity {

    private final Entity handle;

    public NMSEntityImpl(org.bukkit.entity.@NotNull @NotNull Entity entity) {
        super();
        this.handle = ((CraftEntity) entity).getHandle();
    }

    @Generated
    public Entity getHandle() {
        return this.handle;
    }
}
