package io.github.hello09x.fakeplayer.core.util;

import java.util.NoSuchElementException;
import org.bukkit.attribute.Attribute;
import org.jetbrains.annotations.NotNull;

public abstract class Attributes {
   public Attributes() {
   }

   @NotNull
   public static Attribute maxHealth() {
      try {
         return (Attribute)Attribute.class.getField("GENERIC_MAX_HEALTH").get(Attribute.class);
      } catch (IllegalAccessException | NoSuchFieldException var3) {
         try {
            return (Attribute)Attribute.class.getField("MAX_HEALTH").get(Attribute.class);
         } catch (Exception var2) {
            throw new NoSuchElementException("No attribute found for MAX_HEALTH or GENERIC_MAX_HEALTH");
         }
      }
   }

   @NotNull
   public static Attribute playerEntityInteractionRange() {
      try {
         return (Attribute)Attribute.class.getField("PLAYER_ENTITY_INTERACTION_RANGE").get(Attribute.class);
      } catch (IllegalAccessException | NoSuchFieldException var1) {
         throw new NoSuchElementException("No attribute found for PLAYER_ENTITY_INTERACTION_RANGE");
      }
   }
}
