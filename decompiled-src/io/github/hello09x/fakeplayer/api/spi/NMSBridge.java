package io.github.hello09x.fakeplayer.api.spi;

import java.net.InetAddress;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface NMSBridge {

    @NotNull
    NMSEntity fromEntity(@NotNull Entity entity);

    @NotNull
    NMSServer fromServer(@NotNull Server server);

    @NotNull
    NMSServerLevel fromWorld(@NotNull World world);

    @NotNull
    NMSServerPlayer fromPlayer(@NotNull Player player);

    @NotNull
    NMSNetwork createNetwork(@NotNull InetAddress inetaddress);

    boolean isSupported();

    @NotNull
    ActionTicker createAction(@NotNull Player player, @NotNull ActionType actiontype, @NotNull ActionSetting actionsetting);

    boolean criticalAttack(@NotNull Player player);

    boolean maceAttack(@NotNull Player player);

    boolean placeHeldBlock(@NotNull Player player, double d0, @NotNull PlaceSide placeside);

    boolean placeCrystal(@NotNull Player player, double d0);

    boolean breakCrystal(@NotNull Player player, double d0);

    boolean useLavaBucketOnTarget(@NotNull Player player, @NotNull Player player1, double d0);

    boolean pickupFluid(@NotNull Player player, double d0);
}
