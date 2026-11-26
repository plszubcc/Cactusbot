package io.github.hello09x.fakeplayer.api.spi;

import lombok.Generated;
import net.kyori.adventure.translation.Translatable;
import org.jetbrains.annotations.NotNull;

public enum ActionType implements Translatable {
   ATTACK("fakeplayer.action.attack"),
   MINE("fakeplayer.action.mine"),
   USE("fakeplayer.action.use"),
   JUMP("fakeplayer.action.jump"),
   LOOK_AT_NEAREST_ENTITY("fakeplayer.action.look-at-entity"),
   DROP_ITEM("fakeplayer.action.drop-item"),
   DROP_STACK("fakeplayer.action.drop-stack"),
   DROP_INVENTORY("fakeplayer.action.drop-inventory");

   final String translationKey;

   @NotNull
   public String translationKey() {
      return this.translationKey;
   }

   @Generated
   private ActionType(final String translationKey) {
      this.translationKey = translationKey;
   }
}
