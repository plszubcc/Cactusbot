package io.github.hello09x.fakeplayer.core.command.impl;

import com.coderxi.plugin.utils.translation.MessageUtils;
import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class SprintCommand extends AbstractCommand {
   public SprintCommand() {
   }

   public void sprint(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
      try {
         Player fake = super.getFakeplayer(sender, args);
         String state = args.getOptional("state").map(String.class::cast).orElse("");
         boolean sprinting = "on".equalsIgnoreCase(state);
         fake.setSprinting(sprinting);
      } catch (WrapperCommandSyntaxException var6) {
         throw var6;
      } catch (Throwable var7) {
         log.warning("Sprint command failed: " + var7.getClass().getSimpleName() + ": " + var7.getMessage());
         sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.spawn.error.invalid-arguments").color(NamedTextColor.GRAY));
      }
   }
}
