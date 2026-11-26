package io.github.hello09x.fakeplayer.v1_21_11.action;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig.EnumPlayerDigType;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.EnumHand;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPosition.EnumMovingObjectType;
import org.jetbrains.annotations.Nullable;

public class MineAction extends TraceAction {
   private final MineAction.Current current = new MineAction.Current();

   public MineAction(EntityPlayer player) {
      super(player);
   }

   @Override
   public boolean tick() {
      MovingObjectPosition hit = this.getTarget();
      if (hit == null) {
         return false;
      } else if (hit.d() != EnumMovingObjectType.b) {
         return false;
      } else if (this.current.freeze > 0) {
         this.current.freeze--;
         return false;
      } else {
         MovingObjectPositionBlock blockHit = (MovingObjectPositionBlock)hit;
         BlockPosition pos = blockHit.b();
         EnumDirection side = blockHit.c();
         if (this.player.a(this.player.A(), pos, this.player.h.b())) {
            return false;
         } else if (this.current.pos != null && this.player.A().a_(this.current.pos).l()) {
            this.current.pos = null;
            return false;
         } else {
            IBlockData state = this.player.A().a_(pos);
            boolean broken = false;
            if (this.player.h.b().g()) {
               this.player.h.a(pos, EnumPlayerDigType.a, side, this.player.A().aw(), -1);
               this.current.freeze = 5;
               broken = true;
            } else if (this.current.pos != null && this.current.pos.equals(pos)) {
               this.current.progress = this.current.progress + state.a(this.player, this.player.A(), pos);
               if (this.current.progress >= 1.0F) {
                  this.player.h.a(pos, EnumPlayerDigType.c, side, this.player.A().aw(), -1);
                  this.current.pos = null;
                  this.current.freeze = 5;
                  broken = true;
               }

               this.player.A().a(-1, pos, (int)(this.current.progress * 10.0F));
            } else {
               if (this.current.pos != null) {
                  this.player.h.a(this.current.pos, EnumPlayerDigType.b, side, this.player.A().aw(), -1);
               }

               this.player.h.a(pos, EnumPlayerDigType.a, side, this.player.A().aw(), -1);
               if (!state.l() && this.current.progress == 0.0F) {
                  state.a(this.player.A(), pos, this.player);
               }

               if (!state.l() && state.a(this.player, this.player.A(), pos) >= 1.0F) {
                  this.current.pos = null;
                  broken = true;
               } else {
                  this.current.pos = pos;
                  this.current.progress = 0.0F;
               }
            }

            this.player.J();
            this.player.a(EnumHand.a);
            return broken;
         }
      }
   }

   @Override
   public void inactiveTick() {
      this.stop();
   }

   @Override
   public void stop() {
      if (this.current.pos != null) {
         this.player.A().a(-1, this.current.pos, -1);
         this.player.h.a(this.current.pos, EnumPlayerDigType.b, EnumDirection.a, this.player.A().aw(), -1);
         this.current.pos = null;
         this.current.freeze = 0;
         this.current.progress = 0.0F;
      }
   }

   private static class Current {
      @Nullable
      public BlockPosition pos;
      public float progress;
      public int freeze;

      private Current() {
      }
   }
}
