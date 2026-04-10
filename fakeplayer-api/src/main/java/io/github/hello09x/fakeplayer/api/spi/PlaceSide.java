package io.github.hello09x.fakeplayer.api.spi;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public enum PlaceSide {

    FRONT, LEFT, RIGHT;

    private PlaceSide() {}

    public static @NotNull PlaceSide from(@NotNull String raw) {
        PlaceSide placeside;

        switch (raw.trim().toLowerCase(Locale.ROOT)) {
            case "left":
                placeside = PlaceSide.LEFT;
                break;
            case "right":
                placeside = PlaceSide.RIGHT;
                break;
            default:
                placeside = PlaceSide.FRONT;
        }

        return placeside;
    }
}
