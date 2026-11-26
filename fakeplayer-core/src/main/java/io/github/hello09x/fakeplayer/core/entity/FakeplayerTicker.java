package io.github.hello09x.fakeplayer.core.entity;

import io.github.hello09x.fakeplayer.api.spi.NMSServerPlayer;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class FakeplayerTicker extends BukkitRunnable {
   public static final long NON_REMOVE_AT = -1L;
   @NotNull
   private final Fakeplayer player;
   private final long removeAt;
   private boolean firstTick;

   public FakeplayerTicker(@NotNull Fakeplayer player, long lifespan) {
      this.player = player;
      this.removeAt = lifespan > 0L ? System.currentTimeMillis() + lifespan : -1L;
      this.firstTick = true;
   }

   public void run() {
      if (!this.player.isOnline()) {
         super.cancel();
      } else if (this.removeAt != -1L && this.player.getTickCount() % 20 == 0 && System.currentTimeMillis() > this.removeAt) {
         ((FakeplayerManager)Main.getInjector().getInstance(FakeplayerManager.class)).remove(this.player.getName(), "lifespan ends");
         super.cancel();
      } else {
         if (this.firstTick) {
            this.doFirstTick();
         } else {
            this.doTick();
         }
      }
   }

   private void doFirstTick() {
      NMSServerPlayer handle = this.player.getHandle();
      Player player = this.player.getPlayer();
      double x = handle.getX();
      double y = handle.getY();
      double z = handle.getZ();
      handle.setXo(x);
      handle.setYo(y);
      handle.setZo(z);
      handle.doTick();
      player.teleport(new Location(player.getWorld(), x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch()));
      handle.absMoveTo(x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch());
      this.firstTick = false;
   }

   private void doTick() {
      NMSServerPlayer handle = this.player.getHandle();
      handle.doTick();
   }
}
