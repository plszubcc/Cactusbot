package io.github.hello09x.fakeplayer.api.spi;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public enum PlaceSide {
   FRONT,
   LEFT,
   RIGHT;

   private PlaceSide() {
   }

   @NotNull
   public static PlaceSide from(@NotNull String raw) {
      String var1 = raw.trim().toLowerCase(Locale.ROOT);

      return switch (var1) {
         case "left" -> LEFT;
         case "right" -> RIGHT;
         default -> FRONT;
      };
   }
}
