package io.github.hello09x.fakeplayer.v1_21_11.network;

import io.github.hello09x.fakeplayer.api.spi.NMSServerGamePacketListener;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity;
import net.minecraft.network.protocol.game.PacketPlayOutRespawn;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FakeServerGamePacketListenerImpl extends PlayerConnection implements NMSServerGamePacketListener {

    private final FakeplayerManager manager = (FakeplayerManager) Main.getInjector().getInstance(FakeplayerManager.class);
    private static final Logger log = Main.getInstance().getLogger();

    public FakeServerGamePacketListenerImpl(@NotNull MinecraftServer server, @NotNull NetworkManager connection, @NotNull EntityPlayer player, @NotNull CommonListenerCookie cookie) {
        super(server, connection, player, cookie);
        Optional.ofNullable(Bukkit.getPlayer(player.cY())).ifPresent((px) -> {
            this.addChannel(px, FakeServerGamePacketListenerImpl.BUNGEE_CORD_CORRECTED_CHANNEL);
        });
    }

    private boolean addChannel(@NotNull Player player, @NotNull String channel) {
        try {
            Method method = player.getClass().getMethod("addChannel", String.class);
            Object ret = method.invoke(player, channel);

            if (ret instanceof Boolean success) {
                return success;
            } else {
                return true;
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException nosuchmethodexception) {
            return false;
        }
    }

    public void b(Packet<?> packet) {
        if (packet instanceof ClientboundCustomPayloadPacket p) {
            this.handleCustomPayloadPacket(p);
        } else if (packet instanceof PacketPlayOutEntityVelocity p) {
            this.handleClientboundSetEntityMotionPacket(p);
        } else if (packet instanceof PacketPlayOutRespawn p) {
            this.handleClientboundRespawnPacket(p);
        }

    }

    public void handleClientboundSetEntityMotionPacket(@NotNull PacketPlayOutEntityVelocity packet) {
        if (packet.b() == this.g.aA() && this.g.ah) {
            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                this.g.ah = true;
                Vec3D movement = packet.e();

                this.g.j(movement);
            });
        }

    }

    private void handleCustomPayloadPacket(@NotNull ClientboundCustomPayloadPacket packet) {
        CustomPacketPayload payload = packet.b();
        MinecraftKey resourceLocation = payload.a().a();
        String s = resourceLocation.b();
        String channel = s + ":" + resourceLocation.a();

        if (channel.equals(FakeServerGamePacketListenerImpl.BUNGEE_CORD_CORRECTED_CHANNEL)) {
            if (payload instanceof DiscardedPayload) {
                DiscardedPayload discardedPayload = (DiscardedPayload) payload;
                Stream stream = Bukkit.getOnlinePlayers().stream();
                FakeplayerManager fakeplayermanager = this.manager;

                Objects.requireNonNull(this.manager);
                Player recipient = (Player) stream.filter(fakeplayermanager::isNotFake).findAny().orElse((Object) null);

                if (recipient == null) {
                    FakeServerGamePacketListenerImpl.log.warning("Failed to forward a plugin message cause non real players in the server");
                } else {
                    byte[] message = this.getDiscardedPayloadData(discardedPayload);

                    recipient.sendPluginMessage(Main.getInstance(), "BungeeCord", message);
                }
            }
        }
    }

    private void handleClientboundRespawnPacket(@NotNull PacketPlayOutRespawn packet) {
        this.g.T();
    }

    private byte[] getDiscardedPayloadData(@NotNull DiscardedPayload payload) {
        try {
            return payload.data().array();
        } catch (NoSuchMethodError nosuchmethoderror) {
            try {
                return (byte[]) payload.getClass().getMethod("data").invoke(payload);
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException illegalaccessexception) {
                throw nosuchmethoderror;
            }
        }
    }
}
