package io.github.hello09x.fakeplayer.core.constant;

import lombok.Generated;
import net.kyori.adventure.translation.Translatable;
import org.jetbrains.annotations.NotNull;

public enum Direction implements Translatable {

    DOWN("fakeplayer.direction.down"), UP("fakeplayer.direction.up"), NORTH("fakeplayer.direction.north"), SOUTH("fakeplayer.direction.south"), WEST("fakeplayer.direction.west"), EAST("fakeplayer.direction.east");

    final String translationKey;

    public @NotNull String translationKey() {
        return this.translationKey;
    }

    @Generated
    private Direction(String translationKey) {
        this.translationKey = translationKey;
    }
}
