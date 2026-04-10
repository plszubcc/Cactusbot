package io.github.hello09x.fakeplayer.v1_21_11.action;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.EntityItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;
import org.jetbrains.annotations.NotNull;

public class UseAction extends TraceAction {

    private final UseAction.Current current = new UseAction.Current();

    public UseAction(@NotNull EntityPlayer player) {
        super(player);
    }

    public boolean tick() {
        if (this.current.freeze > 0) {
            --this.current.freeze;
            return false;
        } else if (this.player.fZ()) {
            return true;
        } else {
            MovingObjectPosition hit = this.getTarget();

            if (hit == null) {
                return false;
            } else {
                for (EnumHand hand : EnumHand.values()) {
                    switch (hit.d()) {
                        case b:
                            this.player.J();
                            WorldServer world = this.player.A();
                            MovingObjectPositionBlock blockHit = (MovingObjectPositionBlock) hit;
                            BlockPosition pos = blockHit.b();
                            EnumDirection side = blockHit.c();

                            if (pos.v() < this.player.A().aw() - (side == EnumDirection.b ? 1 : 0) && world.a(this.player, pos)) {
                                EnumInteractionResult result = this.player.h.a(this.player, world, this.player.b(hand), hand, blockHit);

                                if (result.a()) {
                                    this.player.a(hand);
                                    this.current.freeze = 3;
                                    return true;
                                }
                            }
                            break;
                        case c:
                            this.player.J();
                            MovingObjectPositionEntity entityHit = (MovingObjectPositionEntity) hit;
                            Entity entity = entityHit.a();
                            boolean handWasEmpty = this.player.b(hand).f();
                            boolean itemFrameEmpty = entity instanceof EntityItemFrame && ((EntityItemFrame) entity).w().f();
                            Vec3D pos = entityHit.g().a(entity.dP(), entity.dR(), entity.dV());

                            if (entity.a(this.player, pos, hand).a()) {
                                this.current.freeze = 3;
                                return true;
                            }

                            if (this.player.a(entity, hand).a() && (!handWasEmpty || !itemFrameEmpty)) {
                                this.current.freeze = 3;
                                return true;
                            }
                    }

                    ItemStack handItem = this.player.b(hand);

                    if (this.player.h.a(this.player, this.player.A(), handItem, hand).a()) {
                        this.player.J();
                        this.current.freeze = 3;
                        return true;
                    }
                }

                return false;
            }
        }
    }

    public void inactiveTick() {}

    public void stop() {
        this.current.freeze = 0;
        this.player.ge();
    }

    private static final class Current {

        public int freeze;

        private Current() {
            super();
        }
    }
}
