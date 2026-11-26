package io.github.hello09x.fakeplayer.core.command.impl;

import com.coderxi.plugin.utils.translation.MessageUtils;
import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.wrappers.CommandResult;
import io.github.hello09x.fakeplayer.core.Main;
import java.util.Objects;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class CmdCommand extends AbstractCommand {
   private final Logger log = Main.getInstance().getLogger();

   public CmdCommand() {
   }

   public void cmd(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
      Player fake = super.getFakeplayer(sender, args);
      CommandResult command = Objects.requireNonNull((CommandResult)args.get("command"));
      String name = command.command().getName();
      if (!sender.hasPermission("cactusbot.command.cmd") && !this.config.getAllowCommands().contains(name)) {
         sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.cmd.error.no-permission", NamedTextColor.RED));
      } else if (sender.isOp() || !name.equals("fakeplayer") && !name.equals("fp")) {
         if (!command.command().testPermission(fake)) {
            sender.sendMessage(
               MessageUtils.translatableWithPrefix("fakeplayer.command.cmd.error.fakeplayer-has-no-permission", Component.text(fake.getName()))
                  .color(NamedTextColor.RED)
            );
         } else if (!command.execute(fake)) {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.cmd.error.execute-failed", NamedTextColor.RED));
         } else {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.generic.success", NamedTextColor.GRAY));
            this.log.info("%s issued server command: %s".formatted(fake.getName(), stringifyCommand(command)));
         }
      } else {
         sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.cmd.error.no-permission", NamedTextColor.RED));
      }
   }

   @NotNull
   private static String stringifyCommand(@NotNull CommandResult command) {
      StringBuilder builder = new StringBuilder("/");
      builder.append(command.command().getName());
      if (command.args() != null && command.args().length > 0) {
         builder.append(" ").append(String.join(" ", command.args()));
      }

      return builder.toString();
   }
}
