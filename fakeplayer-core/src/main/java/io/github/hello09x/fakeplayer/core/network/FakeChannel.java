package io.github.hello09x.fakeplayer.core.network;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FakeChannel extends AbstractChannel {

    private static final EventLoop EVENT_LOOP = new DefaultEventLoop();
    private final ChannelConfig config = new DefaultChannelConfig(this);
    private final ChannelPipeline pipeline = new FakeChannelPipeline(this);
    private final InetAddress address;

    public FakeChannel(@Nullable Channel parent, @NotNull InetAddress address) {
        super(parent);
        this.address = address;
    }

    public ChannelConfig config() {
        this.config.setAutoRead(true);
        return this.config;
    }

    protected void doBeginRead() throws Exception {}

    protected void doBind(SocketAddress arg0) throws Exception {}

    protected void doClose() throws Exception {}

    protected void doDisconnect() throws Exception {}

    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        while (true) {
            Object msg = in.current();

            if (msg == null) {
                return;
            }

            in.remove();
        }
    }

    public boolean isActive() {
        return true;
    }

    protected boolean isCompatible(EventLoop arg0) {
        return true;
    }

    public boolean isOpen() {
        return true;
    }

    public ChannelPipeline pipeline() {
        return this.pipeline;
    }

    protected SocketAddress localAddress0() {
        return new InetSocketAddress(this.address, 25565);
    }

    public ChannelMetadata metadata() {
        return new ChannelMetadata(true);
    }

    protected AbstractUnsafe newUnsafe() {
        return new AbstractUnsafe() {
            public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
                this.safeSetSuccess(promise);
            }
        };
    }

    protected SocketAddress remoteAddress0() {
        return new InetSocketAddress(this.address, 25565);
    }

    public EventLoop eventLoop() {
        return FakeChannel.EVENT_LOOP;
    }
}
