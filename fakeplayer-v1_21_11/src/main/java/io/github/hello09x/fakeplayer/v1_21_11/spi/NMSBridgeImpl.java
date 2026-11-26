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
   }

   @NotNull
   @Override
   public NMSEntity fromEntity(@NotNull Entity entity) {
      return new NMSEntityImpl(entity);
   }

   @NotNull
   @Override
   public NMSServer fromServer(@NotNull Server server) {
      return new NMSServerImpl(server);
   }

   @NotNull
   @Override
   public NMSServerLevel fromWorld(@NotNull World world) {
      return new NMSServerLevelImpl(world);
   }

   @NotNull
   @Override
   public NMSServerPlayer fromPlayer(@NotNull Player player) {
      return new NMSServerPlayerImpl(player);
   }

   @NotNull
   @Override
   public NMSNetwork createNetwork(@NotNull InetAddress address) {
      return new NMSNetworkImpl(address);
   }

   @Override
   public boolean isSupported() {
      return SUPPORTS.contains(Bukkit.getMinecraftVersion());
   }

   @NotNull
   @Override
   public ActionTicker createAction(@NotNull Player player, @NotNull ActionType action, @NotNull ActionSetting setting) {
      return new ActionTickerImpl((NMSBridge)Main.getInjector().getInstance(NMSBridge.class), player, action, setting);
   }

   @Override
   public boolean criticalAttack(@NotNull Player player) {
      return new CriticalAttackAction(((CraftPlayer)player).getHandle()).tick();
   }

   @Override
   public boolean maceAttack(@NotNull Player player) {
      return new MaceAttackAction(((CraftPlayer)player).getHandle()).tick();
   }

   @Override
   public boolean placeHeldBlock(@NotNull Player player, double range, @NotNull PlaceSide side) {
      return InteractionAction.placeHeldBlock(((CraftPlayer)player).getHandle(), range, side);
   }

   @Override
   public boolean placeCrystal(@NotNull Player player, double range) {
      return InteractionAction.placeCrystal(((CraftPlayer)player).getHandle(), range);
   }

   @Override
   public boolean breakCrystal(@NotNull Player player, double range) {
      return InteractionAction.breakCrystal(((CraftPlayer)player).getHandle(), range);
   }

   @Override
   public boolean useLavaBucketOnTarget(@NotNull Player player, @NotNull Player target, double range) {
      return InteractionAction.useLavaBucketOnTarget(((CraftPlayer)player).getHandle(), ((CraftPlayer)target).getHandle(), range);
   }

   @Override
   public boolean pickupFluid(@NotNull Player player, double range) {
      return InteractionAction.pickupFluid(((CraftPlayer)player).getHandle(), range);
   }
}
