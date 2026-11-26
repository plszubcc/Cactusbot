package io.github.hello09x.fakeplayer.core.command.impl;

import com.coderxi.plugin.utils.translation.MessageUtils;
import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class SneakCommand extends AbstractCommand {
   public SneakCommand() {
   }

   public void sneak(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
      Player fake = this.getFakeplayer(sender, args);
      fake.setSneaking(true);
      sender.sendMessage(
         MessageUtils.translatableWithPrefix("fakeplayer.command.sneak.success.enabled", Component.text(fake.getName(), NamedTextColor.WHITE))
            .color(NamedTextColor.GRAY)
      );
   }

   public void unsneak(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
      Player fake = this.getFakeplayer(sender, args);
      fake.setSneaking(false);
      sender.sendMessage(
         MessageUtils.translatableWithPrefix("fakeplayer.command.sneak.success.disabled", Component.text(fake.getName(), NamedTextColor.WHITE))
            .color(NamedTextColor.GRAY)
      );
   }
}
