package io.github.hello09x.fakeplayer.v1_21_11.action;

import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.GlideEquipmentManager;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPosition.EnumMovingObjectType;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

public class MaceAttackAction extends TraceAction {

    private static final @Nullable Field FALL_DISTANCE_FIELD = getField(Entity.class, "fallDistance");
    private static final @Nullable Field ON_GROUND_FIELD = getField(Entity.class, "onGround");
    private static final @Nullable Field ATTACK_STRENGTH_TICKER_FIELD = getField(EntityLiving.class, "attackStrengthTicker");
    private final EntityPlayer player;

    public MaceAttackAction(EntityPlayer player) {
        super(player);
        this.player = player;
    }

    public boolean tick() {
        MovingObjectPosition hit = this.getTarget();

        return hit != null && hit.d() == EnumMovingObjectType.c ? this.attack(((MovingObjectPositionEntity) hit).a()) : false;
    }

    public boolean attack(Entity target) {
        float oldFallDistance = (float) this.player.an;
        boolean oldOnGround = this.player.aV();
        boolean wasFallFlying = this.player.gj();
        Vec3D oldVelocity = this.player.dN();
        int oldAttackStrengthTicker = getIntField(MaceAttackAction.ATTACK_STRENGTH_TICKER_FIELD, this.player);

        boolean flag;

        try {
            if (wasFallFlying) {
                stopFallFlying(this.player);
            }

            this.player.an = (double) Math.max(3.5F, (float) this.player.an);
            setField(MaceAttackAction.FALL_DISTANCE_FIELD, this.player, this.player.an);
            setField(MaceAttackAction.ON_GROUND_FIELD, this.player, false);
            setField(MaceAttackAction.ATTACK_STRENGTH_TICKER_FIELD, this.player, Math.max(oldAttackStrengthTicker, (int) Math.ceil((double) this.player.he()) + 1));
            if (this.player.dN().h >= -0.08D) {
                this.player.m(oldVelocity.g, -0.35D, oldVelocity.i);
            }

            this.player.e(target);
            this.player.a(EnumHand.a);
            this.player.hf();
            this.player.J();
            ((GlideEquipmentManager) Main.getInjector().getInstance(GlideEquipmentManager.class)).restoreChestplate(this.player.getBukkitEntity());
            flag = true;
        } finally {
            this.player.an = (double) oldFallDistance;
            setField(MaceAttackAction.FALL_DISTANCE_FIELD, this.player, oldFallDistance);
            setField(MaceAttackAction.ON_GROUND_FIELD, this.player, oldOnGround);
            if (wasFallFlying) {
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    if (this.player.getBukkitEntity().isOnline()) {
                        startFallFlying(this.player);
                    }

                }, 1L);
            }

        }

        return flag;
    }

    public void inactiveTick() {}

    public void stop() {}

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

    private static int getIntField(@Nullable Field field, Object instance) {
        if (field == null) {
            return 0;
        } else {
            try {
                return field.getInt(instance);
            } catch (Exception exception) {
                return 0;
            }
        }
    }

    private static void stopFallFlying(EntityPlayer player) {
        try {
            Method method = EntityHuman.class.getMethod("stopFallFlying");

            method.invoke(player);
        } catch (Exception exception) {
            try {
                Method method = Entity.class.getDeclaredMethod("setSharedFlag", Integer.TYPE, Boolean.TYPE);

                method.setAccessible(true);
                method.invoke(player, 7, false);
            } catch (Exception exception1) {
                ;
            }

        }
    }

    private static void startFallFlying(EntityPlayer player) {
        try {
            Method method = EntityHuman.class.getMethod("startFallFlying");

            method.invoke(player);
        } catch (Exception exception) {
            ;
        }

        try {
            Method method = Entity.class.getDeclaredMethod("setSharedFlag", Integer.TYPE, Boolean.TYPE);

            method.setAccessible(true);
            method.invoke(player, 7, true);
        } catch (Exception exception1) {
            ;
        }

    }
}
