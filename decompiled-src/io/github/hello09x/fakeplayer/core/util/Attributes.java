package io.github.hello09x.fakeplayer.core.util;

import java.util.NoSuchElementException;
import org.bukkit.attribute.Attribute;
import org.jetbrains.annotations.NotNull;

public abstract class Attributes {

    public Attributes() {
        super();
    }

    public static @NotNull Attribute maxHealth() {
        try {
            return (Attribute) Attribute.class.getField("GENERIC_MAX_HEALTH").get(Attribute.class);
        } catch (IllegalAccessException | NoSuchFieldException nosuchfieldexception) {
            try {
                return (Attribute) Attribute.class.getField("MAX_HEALTH").get(Attribute.class);
            } catch (Exception exception) {
                throw new NoSuchElementException("No attribute found for MAX_HEALTH or GENERIC_MAX_HEALTH");
            }
        }
    }

    public static @NotNull Attribute playerEntityInteractionRange() {
        try {
            return (Attribute) Attribute.class.getField("PLAYER_ENTITY_INTERACTION_RANGE").get(Attribute.class);
        } catch (IllegalAccessException | NoSuchFieldException nosuchfieldexception) {
            throw new NoSuchElementException("No attribute found for PLAYER_ENTITY_INTERACTION_RANGE");
        }
    }
}
