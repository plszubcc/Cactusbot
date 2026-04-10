package io.github.hello09x.fakeplayer.api.spi;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public interface NMSServerPlayer {

    @NotNull
    Player getPlayer();

    double getX();

    double getY();

    double getZ();

    void setXo(double d0);

    void setYo(double d0);

    void setZo(double d0);

    void doTick();

    void absMoveTo(double d0, double d1, double d2, float f, float f1);

    float getYRot();

    void setYRot(float f);

    float getXRot();

    void setXRot(float f);

    float getZza();

    void setZza(float f);

    float getXxa();

    void setXxa(float f);

    void setDeltaMovement(@NotNull Vector vector);

    boolean startRiding(@NotNull Entity entity, boolean flag);

    void stopRiding();

    void disableAdvancements(@NotNull Plugin plugin);

    int getTickCount();

    void drop(int i, boolean flag, boolean flag1);

    void drop(boolean flag);

    void resetLastActionTime();

    boolean onGround();

    void jumpFromGround();

    void setJumping(boolean flag);

    boolean isUsingItem();

    void setPlayBefore();

    void setupClientOptions();

    void respawn();

    void swapItemWithOffhand();

    void startFallFlying();
}
