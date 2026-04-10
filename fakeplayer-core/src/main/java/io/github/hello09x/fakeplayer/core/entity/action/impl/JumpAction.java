package io.github.hello09x.fakeplayer.core.entity.action.impl;

import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.api.spi.NMSServerPlayer;
import org.jetbrains.annotations.NotNull;

public class JumpAction implements Action {

    private final @NotNull NMSServerPlayer player;

    public JumpAction(@NotNull NMSServerPlayer player) {
        super();
        this.player = player;
    }

    public boolean tick() {
        if (this.player.onGround()) {
            this.player.jumpFromGround();
        } else {
            this.player.setJumping(true);
        }

        this.player.resetLastActionTime();
        return true;
    }

    public void inactiveTick() {
        this.player.setJumping(false);
    }

    public void stop() {
        this.player.setJumping(false);
    }
}
