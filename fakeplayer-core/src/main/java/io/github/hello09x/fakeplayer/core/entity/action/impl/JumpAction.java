package io.github.hello09x.fakeplayer.core.entity.action.impl;

import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.api.spi.NMSServerPlayer;
import org.jetbrains.annotations.NotNull;

public class JumpAction implements Action {
   @NotNull
   private final NMSServerPlayer player;

   public JumpAction(@NotNull NMSServerPlayer player) {
      this.player = player;
   }

   @Override
   public boolean tick() {
      if (this.player.onGround()) {
         this.player.jumpFromGround();
      } else {
         this.player.setJumping(true);
      }

      this.player.resetLastActionTime();
      return true;
   }

   @Override
   public void inactiveTick() {
      this.player.setJumping(false);
   }

   @Override
   public void stop() {
      this.player.setJumping(false);
   }
}
