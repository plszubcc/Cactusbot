package io.github.hello09x.fakeplayer.v1_21_11.spi;

import io.github.hello09x.fakeplayer.api.spi.ActionSetting;
import io.github.hello09x.fakeplayer.api.spi.ActionTicker;
import io.github.hello09x.fakeplayer.api.spi.ActionType;
import io.github.hello09x.fakeplayer.api.spi.NMSBridge;
import io.github.hello09x.fakeplayer.api.spi.NMSEntity;
import io.github.hello09x.fakeplayer.api.spi.NMSNetwork;
import io.github.hello09x.fakeplayer.api.spi.NMSServer;
import io.github.hello09x.fakeplayer.api.spi.NMSServerLevel;
import io.github.hello09x.fakeplayer.api.spi.NMSServerPlayer;
import io.github.hello09x.fakeplayer.api.spi.PlaceSide;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.v1_21_11.action.CriticalAttackAction;
import io.github.hello09x.fakeplayer.v1_21_11.action.InteractionAction;
import io.github.hello09x.fakeplayer.v1_21_11.action.MaceAttackAction;
import java.net.InetAddress;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NMSBridgeImpl implements NMSBridge {

    private static final Set<String> SUPPORTS = Set.of("1.21.11");

    public NMSBridgeImpl() {
        super();
    }

    public @NotNull NMSEntity fromEntity(@NotNull Entity entity) {
        return new NMSEntityImpl(entity);
    }

    public @NotNull NMSServer fromServer(@NotNull Server server) {
        return new NMSServerImpl(server);
    }

    public @NotNull NMSServerLevel fromWorld(@NotNull World world) {
        return new NMSServerLevelImpl(world);
    }

    public @NotNull NMSServerPlayer fromPlayer(@NotNull Player player) {
        return new NMSServerPlayerImpl(player);
    }

    public @NotNull NMSNetwork createNetwork(@NotNull InetAddress address) {
        return new NMSNetworkImpl(address);
    }

    public boolean isSupported() {
        return NMSBridgeImpl.SUPPORTS.contains(Bukkit.getMinecraftVersion());
    }

    public @NotNull ActionTicker createAction(@NotNull Player player, @NotNull ActionType action, @NotNull ActionSetting setting) {
        return new ActionTickerImpl((NMSBridge) Main.getInjector().getInstance(NMSBridge.class), player, action, setting);
    }

    public boolean criticalAttack(@NotNull Player player) {
        return (new CriticalAttackAction(((CraftPlayer) player).getHandle())).tick();
    }

    public boolean maceAttack(@NotNull Player player) {
        return (new MaceAttackAction(((CraftPlayer) player).getHandle())).tick();
    }

    public boolean placeHeldBlock(@NotNull Player player, double range, @NotNull PlaceSide side) {
        return InteractionAction.placeHeldBlock(((CraftPlayer) player).getHandle(), range, side);
    }

    public boolean placeCrystal(@NotNull Player player, double range) {
        return InteractionAction.placeCrystal(((CraftPlayer) player).getHandle(), range);
    }

    public boolean breakCrystal(@NotNull Player player, double range) {
        return InteractionAction.breakCrystal(((CraftPlayer) player).getHandle(), range);
    }

    public boolean useLavaBucketOnTarget(@NotNull Player player, @NotNull Player target, double range) {
        return InteractionAction.useLavaBucketOnTarget(((CraftPlayer) player).getHandle(), ((CraftPlayer) target).getHandle(), range);
    }

    public boolean pickupFluid(@NotNull Player player, double range) {
        return InteractionAction.pickupFluid(((CraftPlayer) player).getHandle(), range);
    }
}
