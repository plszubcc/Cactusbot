package io.github.hello09x.fakeplayer.core.util;

import java.net.InetSocketAddress;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AddressUtils {
   public AddressUtils() {
   }

   @NotNull
   public static String getAddress(@NotNull CommandSender sender) {
      return sender instanceof Player p ? Optional.ofNullable(p.getAddress()).map(InetSocketAddress::getHostString).orElse("0.0.0.0") : "127.0.0.1";
   }
}
