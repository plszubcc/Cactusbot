package io.github.hello09x.fakeplayer.api.spi;

import java.net.InetAddress;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface NMSBridge {
   @NotNull
   NMSEntity fromEntity(@NotNull Entity var1);

   @NotNull
   NMSServer fromServer(@NotNull Server var1);

   @NotNull
   NMSServerLevel fromWorld(@NotNull World var1);

   @NotNull
   NMSServerPlayer fromPlayer(@NotNull Player var1);

   @NotNull
   NMSNetwork createNetwork(@NotNull InetAddress var1);

   boolean isSupported();

   @NotNull
   ActionTicker createAction(@NotNull Player var1, @NotNull ActionType var2, @NotNull ActionSetting var3);

   boolean criticalAttack(@NotNull Player var1);

   boolean maceAttack(@NotNull Player var1);

   boolean placeHeldBlock(@NotNull Player var1, double var2, @NotNull PlaceSide var4);

   boolean placeCrystal(@NotNull Player var1, double var2);

   boolean breakCrystal(@NotNull Player var1, double var2);

   boolean useLavaBucketOnTarget(@NotNull Player var1, @NotNull Player var2, double var3);

   boolean pickupFluid(@NotNull Player var1, double var2);
}
