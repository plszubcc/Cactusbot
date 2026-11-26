package io.github.hello09x.fakeplayer.core.command.impl;

import com.coderxi.plugin.utils.translation.MessageUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.core.translation.PluginTranslator;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Singleton
public class ReloadCommand extends AbstractCommand {
   private final FakeplayerConfig config;
   private final PluginTranslator translator;

   @Inject
   public ReloadCommand(FakeplayerConfig config, PluginTranslator translator) {
      this.config = config;
      this.translator = translator;
   }

   public void reload(@NotNull CommandSender sender, @NotNull CommandArguments args) {
      this.config.reload();
      sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.generic.success", NamedTextColor.GRAY));
      if (this.config.isConfigFileOutOfDate()) {
         sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.configuration.out-of-date", NamedTextColor.GRAY));
      }
   }

   public void reloadTranslation(@NotNull CommandSender sender, @NotNull CommandArguments args) {
      this.translator.reload();
      sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.generic.success"));
   }
}
