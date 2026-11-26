package io.github.hello09x.fakeplayer.core.manager.feature;

import io.github.hello09x.fakeplayer.core.repository.model.Feature;
import org.jetbrains.annotations.NotNull;

public record FeatureInstance(@NotNull Feature key, @NotNull String value) {
   @NotNull
   public String asString() {
      return this.value;
   }

   public boolean asBoolean() {
      return Boolean.parseBoolean(this.value);
   }
}
