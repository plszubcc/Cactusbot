package io.github.hello09x.fakeplayer.core.config;

import org.jetbrains.annotations.Nullable;

public record PresetConditions(@Nullable Double selfHealthMin, @Nullable Double selfHealthMax, @Nullable Boolean selfOnFire, @Nullable Double targetHealthMin, @Nullable Double targetHealthMax, @Nullable Double targetDistanceMin, @Nullable Double targetDistanceMax, @Nullable Boolean targetBlocking, @Nullable Boolean targetHoldingAxe, @Nullable Boolean targetInCobweb, @Nullable Boolean targetHit, @Nullable Integer targetHitWithinTicks, @Nullable String afterCommand, @Nullable Integer afterCommandWithinTicks) {

    public PresetConditions {
        super();
    }

    public boolean isEmpty() {
        return this.selfHealthMin == null && this.selfHealthMax == null && this.selfOnFire == null && this.targetHealthMin == null && this.targetHealthMax == null && this.targetDistanceMin == null && this.targetDistanceMax == null && this.targetBlocking == null && this.targetHoldingAxe == null && this.targetInCobweb == null && this.targetHit == null && this.targetHitWithinTicks == null && this.afterCommand == null && this.afterCommandWithinTicks == null;
    }
}
