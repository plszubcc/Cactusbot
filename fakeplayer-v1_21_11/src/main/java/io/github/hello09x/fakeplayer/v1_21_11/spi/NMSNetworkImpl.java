package io.github.hello09x.fakeplayer.v1_21_11.spi;

import io.github.hello09x.fakeplayer.api.spi.NMSNetwork;
import io.github.hello09x.fakeplayer.api.spi.NMSServerGamePacketListener;
import io.github.hello09x.fakeplayer.v1_21_11.network.FakeConnection;
import io.github.hello09x.fakeplayer.v1_21_11.network.FakeServerGamePacketListenerImpl;
import java.net.InetAddress;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_21_R7.CraftServer;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NMSNetworkImpl implements NMSNetwork {
   @NotNull
   private final FakeConnection connection;
   private NMSServerGamePacketListener serverGamePacketListener;

   public NMSNetworkImpl(@NotNull InetAddress address) {
      this.connection = new FakeConnection(address);
   }

   @NotNull
   @Override
   public NMSServerGamePacketListener placeNewPlayer(@NotNull Server server, @NotNull Player player) {
      EntityPlayer handle = ((CraftPlayer)player).getHandle();
      CommonListenerCookie cookie = CommonListenerCookie.a(((CraftPlayer)player).getProfile(), false);
      ((CraftServer)server).getHandle().a(this.connection, handle, cookie);
      FakeServerGamePacketListenerImpl listener = new FakeServerGamePacketListenerImpl(((CraftServer)server).getServer(), this.connection, handle, cookie);
      this.serverGamePacketListener = listener;
      handle.g = listener;
      return listener;
   }

   @NotNull
   @Override
   public NMSServerGamePacketListener getServerGamePacketListener() throws IllegalStateException {
      return this.serverGamePacketListener;
   }
}
