package io.github.hello09x.devtools.core.utils;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageRecipient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BungeeCordUtils {

    public static final String CHANNEL = "BungeeCord";

    public BungeeCordUtils() {
        super();
    }

    public static boolean getServer(@NotNull Plugin plugin) {
        return sendPluginMessage(plugin, () -> {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();

            out.writeUTF("GetServer");
            return out.toByteArray();
        });
    }

    public static boolean getPlayerList(@NotNull Plugin plugin, @NotNull String server) {
        return sendPluginMessage(plugin, () -> {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();

            out.writeUTF("PlayerList");
            out.writeUTF(server);
            return out.toByteArray();
        });
    }

    public static boolean connectOther(@NotNull Plugin plugin, @NotNull String playerName, @NotNull String server) {
        return sendPluginMessage(plugin, () -> {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();

            out.writeUTF("ConnectOther");
            out.writeUTF(playerName);
            out.writeUTF(server);
            return out.toByteArray();
        });
    }

    public static boolean getPlayerList(@NotNull Plugin plugin) {
        return getPlayerList(plugin, "ALL");
    }

    public static boolean sendPluginMessage(@NotNull Plugin plugin, @NotNull Supplier<byte[]> message) {
        Player r = (Player) Iterables.getFirst(Bukkit.getOnlinePlayers(), (Object) null);

        if (r == null) {
            return false;
        } else {
            sendPluginMessage(plugin, r, (byte[]) message.get());
            return true;
        }
    }

    public static void sendPluginMessage(@NotNull Plugin plugin, @NotNull PluginMessageRecipient recipient, byte @NotNull [] message) {
        recipient.sendPluginMessage(plugin, "BungeeCord", message);
    }

    public static boolean sendForwardPluginMessage(@NotNull Plugin plugin, @NotNull String subchannel, @NotNull Supplier<byte[]> message) {
        return sendPluginMessage(plugin, () -> {
            return asForwardMessage(subchannel, (byte[]) message.get());
        });
    }

    public static boolean sendForwardPluginMessage(@NotNull Plugin plugin, @NotNull String subchannel, byte @NotNull [] message) {
        return sendPluginMessage(plugin, () -> {
            return asForwardMessage(subchannel, message);
        });
    }

    public static byte @NotNull [] asForwardMessage(@NotNull String subchannel, @NotNull byte[] message) {
        ByteArrayDataOutput packet = ByteStreams.newDataOutput();

        packet.writeUTF("Forward");
        packet.writeUTF("ONLINE");
        packet.writeUTF(subchannel);
        packet.writeShort(message.length);
        packet.write(message);
        return packet.toByteArray();
    }

    public static byte @NotNull [] asReceivedForwardMessage(@NotNull String subchannel, byte @NotNull [] message) {
        ByteArrayDataOutput packet = ByteStreams.newDataOutput();

        packet.writeUTF(subchannel);
        packet.writeShort(message.length);
        packet.write(message);
        return packet.toByteArray();
    }

    public static @Nullable ByteArrayDataInput parseReceivedForwardMessage(@NotNull String subchannel, byte @NotNull [] received) {
        ByteArrayDataInput in = ByteStreams.newDataInput(received);

        if (!subchannel.equals(in.readUTF())) {
            return null;
        } else {
            short length = in.readShort();
            byte[] message = new byte[length];

            in.readFully(message);
            return ByteStreams.newDataInput(message);
        }
    }

    public interface SubChannel {

        String PLAYER_LIST = "PlayerList";
        String GET_SERVER = "GetServer";
    }
}
