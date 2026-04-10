package io.github.hello09x.fakeplayer.v1_21_11.action;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPosition.EnumMovingObjectType;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import org.jetbrains.annotations.Nullable;

public class CriticalAttackAction extends TraceAction {

    private static final @Nullable Field FALL_DISTANCE_FIELD = getField(Entity.class, "fallDistance");
    private static final @Nullable Field ON_GROUND_FIELD = getField(Entity.class, "onGround");
    private static final @Nullable Method CRIT_METHOD = getCritMethod();
    private final EntityPlayer player;

    public CriticalAttackAction(EntityPlayer player) {
        super(player);
        this.player = player;
    }

    public boolean tick() {
        MovingObjectPosition hit = this.getTarget();

        if (hit != null && hit.d() == EnumMovingObjectType.c) {
            MovingObjectPositionEntity entityHit = (MovingObjectPositionEntity) hit;

            return this.attack(entityHit.a());
        } else {
            return false;
        }
    }

    public boolean attack(Entity target) {
        float oldFallDistance = (float) this.player.an;
        boolean oldOnGround = this.player.aV();

        boolean flag;

        try {
            forceCritState(this.player);
            this.player.e(target);
            this.player.a(EnumHand.a);
            this.player.hf();
            this.player.J();
            this.playCritEffect(target);
            flag = true;
        } finally {
            restoreState(this.player, oldFallDistance, oldOnGround);
        }

        return flag;
    }

    public void inactiveTick() {}

    public void stop() {}

    private static void forceCritState(EntityPlayer player) {
        player.an = Math.max(0.10000000149011612D, player.an);
        setField(CriticalAttackAction.FALL_DISTANCE_FIELD, player, player.an);
        setField(CriticalAttackAction.ON_GROUND_FIELD, player, false);
    }

    private static void restoreState(EntityPlayer player, float fallDistance, boolean onGround) {
        player.an = (double) fallDistance;
        setField(CriticalAttackAction.FALL_DISTANCE_FIELD, player, fallDistance);
        setField(CriticalAttackAction.ON_GROUND_FIELD, player, onGround);
    }

    private void playCritEffect(Entity target) {
        if (CriticalAttackAction.CRIT_METHOD != null) {
            try {
                CriticalAttackAction.CRIT_METHOD.invoke(this.player, target);
            } catch (Exception exception) {
                ;
            }

        }
    }

    private static @Nullable Field getField(Class<?> owner, String name) {
        try {
            Field field = owner.getDeclaredField(name);

            field.setAccessible(true);
            return field;
        } catch (Exception exception) {
            return null;
        }
    }

    private static void setField(@Nullable Field field, Object instance, Object value) {
        if (field != null) {
            try {
                field.set(instance, value);
            } catch (Exception exception) {
                ;
            }

        }
    }

    private static @Nullable Method getCritMethod() {
        try {
            Method method = EntityHuman.class.getDeclaredMethod("crit", Entity.class);

            method.setAccessible(true);
            return method;
        } catch (Exception exception) {
            return null;
        }
    }
}
