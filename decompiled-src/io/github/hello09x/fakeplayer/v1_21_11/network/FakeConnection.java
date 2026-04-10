package io.github.hello09x.fakeplayer.v1_21_11.network;

import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.core.network.FakeChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import java.net.InetAddress;
import java.util.logging.Logger;
import net.minecraft.network.BandwidthDebugMonitor;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.Packet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FakeConnection extends NetworkManager {

    private static final Logger log = Main.getInstance().getLogger();
    private final FakeplayerManager manager = (FakeplayerManager) Main.getInjector().getInstance(FakeplayerManager.class);

    public FakeConnection(@NotNull InetAddress address) {
        super(EnumProtocolDirection.a);
        this.k = new FakeChannel((Channel) null, address);
        this.l = this.k.remoteAddress();
        NetworkManager.a(this.k.pipeline(), EnumProtocolDirection.a, false, (BandwidthDebugMonitor) null);
    }

    public boolean i() {
        return true;
    }

    public void a(Packet<?> packet, @Nullable ChannelFutureListener channelfuturelistener) {}

    public void a(Packet<?> packet, @Nullable ChannelFutureListener channelfuturelistener, boolean flag) {}

    public void a(Packet<?> packet) {}
}
