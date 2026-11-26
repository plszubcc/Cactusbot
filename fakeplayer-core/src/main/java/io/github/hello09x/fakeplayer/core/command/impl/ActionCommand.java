package io.github.hello09x.fakeplayer.core.command.impl;

import com.coderxi.plugin.utils.translation.MessageUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import io.github.hello09x.fakeplayer.api.spi.ActionSetting;
import io.github.hello09x.fakeplayer.api.spi.ActionType;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerAutofishManager;
import io.github.hello09x.fakeplayer.core.manager.action.ActionManager;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class ActionCommand extends AbstractCommand {
   private final ActionManager actionManager;
   private final FakeplayerAutofishManager autofishManager;

   @Inject
   public ActionCommand(ActionManager actionManager, FakeplayerAutofishManager autofishManager) {
      this.actionManager = actionManager;
      this.autofishManager = autofishManager;
   }

   @NotNull
   public CommandExecutor action(@NotNull ActionType action, @NotNull ActionSetting setting) {
      return (sender, args) -> this.action(sender, args, action, setting.clone());
   }

   public void action(@NotNull CommandSender sender, @NotNull CommandArguments args, @NotNull ActionType action, @NotNull ActionSetting setting) throws WrapperCommandSyntaxException {
      Player fake = super.getFakeplayer(sender, args);
      if (action == ActionType.USE && fake.getInventory().getItemInMainHand().getType() == Material.FISHING_ROD && this.autofishManager.isAutofish(fake)) {
         setting = ActionSetting.once();
      }

      this.actionManager.setAction(fake, action, setting);
      if (!setting.equals(ActionSetting.once())) {
         sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.generic.success"));
      }
   }
}
