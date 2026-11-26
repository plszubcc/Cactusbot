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

   void setXo(double var1);

   void setYo(double var1);

   void setZo(double var1);

   void doTick();

   void absMoveTo(double var1, double var3, double var5, float var7, float var8);

   float getYRot();

   void setYRot(float var1);

   float getXRot();

   void setXRot(float var1);

   float getZza();

   void setZza(float var1);

   float getXxa();

   void setXxa(float var1);

   void setDeltaMovement(@NotNull Vector var1);

   boolean startRiding(@NotNull Entity var1, boolean var2);

   void stopRiding();

   void disableAdvancements(@NotNull Plugin var1);

   int getTickCount();

   void drop(int var1, boolean var2, boolean var3);

   void drop(boolean var1);

   void resetLastActionTime();

   boolean onGround();

   void jumpFromGround();

   void setJumping(boolean var1);

   boolean isUsingItem();

   void setPlayBefore();

   void setupClientOptions();

   void respawn();

   void swapItemWithOffhand();

   void startFallFlying();
}
