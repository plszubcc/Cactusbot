package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.core.translation.TranslatorUtils;
import io.github.hello09x.devtools.core.utils.ComponentUtils;
import io.github.hello09x.fakeplayer.core.manager.CustomActionManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class CustomActionCommand extends AbstractCommand {
   private final CustomActionManager customActionManager;

   @Inject
   public CustomActionCommand(CustomActionManager customActionManager) {
      this.customActionManager = customActionManager;
   }

   public void execute(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
      String actionName = String.valueOf(args.get("action"));
      Player bot = this.getFakeplayer(sender, args);
      if (!this.customActionManager.execute(actionName, bot)) {
         throw CommandAPI.failWithString(
            ComponentUtils.toString(Component.translatable("fakeplayer.command.action.error.not-found", actionName), TranslatorUtils.getLocale(sender))
         );
      }
   }
}
