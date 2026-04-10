package io.github.hello09x.fakeplayer.core.entity.action.impl;

import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.api.spi.NMSServerPlayer;
import org.jetbrains.annotations.NotNull;

public class DropItemAction implements Action {

    private final @NotNull NMSServerPlayer player;

    public DropItemAction(@NotNull NMSServerPlayer player) {
        super();
        this.player = player;
    }

    public boolean tick() {
        this.player.drop(false);
        this.player.resetLastActionTime();
        return true;
    }

    public void inactiveTick() {}

    public void stop() {}
}
