package io.github.hello09x.fakeplayer.core.util;

import java.net.InetSocketAddress;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AddressUtils {

    public AddressUtils() {
        super();
    }

    public static @NotNull String getAddress(@NotNull CommandSender sender) {
        if (sender instanceof Player p) {
            return (String) Optional.ofNullable(p.getAddress()).map(InetSocketAddress::getHostString).orElse("0.0.0.0");
        } else {
            return "127.0.0.1";
        }
    }
}
