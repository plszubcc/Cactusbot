package io.github.hello09x.fakeplayer.v1_21_11.action;

import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.v1_21_11.action.util.Tracer;
import java.lang.reflect.Field;
import java.util.List;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.phys.MovingObjectPosition;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftPlayer;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class TraceAction implements Action {

    protected final EntityPlayer player;

    protected TraceAction(@NotNull EntityPlayer player) {
        super();
        this.player = player;
    }

    protected @Nullable MovingObjectPosition getTarget() {
        double reach = this.player.h.e() ? 5.0D : 4.5D;

        try {
            CraftPlayer bukkit = this.player.getBukkitEntity();
            List<MetadataValue> metadata = bukkit.getMetadata("fakeplayer:preset_reach");

            if (metadata != null) {
                for (MetadataValue value : metadata) {
                    if (value != null) {
                        Object object = value.value();

                        if (object instanceof Number) {
                            Number number = (Number) object;

                            if (number.doubleValue() > 0.0D) {
                                reach = number.doubleValue();
                                return Tracer.rayTrace(this.player, 1.0F, reach, false);
                            }
                        }
                    }
                }
            }

            Field attributeField = Class.forName("org.bukkit.attribute.Attribute").getField("PLAYER_ENTITY_INTERACTION_RANGE");
            Attribute attribute = (Attribute) attributeField.get((Object) null);
            AttributeInstance attr = bukkit.getAttribute(attribute);

            if (attr != null && attr.getValue() > 0.0D) {
                reach = attr.getValue();
            }
        } catch (Throwable throwable) {
            ;
        }

        return Tracer.rayTrace(this.player, 1.0F, reach, false);
    }
}
