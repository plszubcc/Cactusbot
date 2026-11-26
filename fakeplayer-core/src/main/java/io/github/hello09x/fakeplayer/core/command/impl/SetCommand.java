package io.github.hello09x.fakeplayer.core.command.impl;

import com.coderxi.plugin.utils.translation.MessageUtils;
import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.repository.model.Feature;
import java.util.Objects;
import java.util.function.BiConsumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class SetCommand extends AbstractCommand {
   public SetCommand() {
   }

   public void set(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
      Player target = super.getFakeplayer(sender, args);
      Feature feature = Objects.requireNonNull((Feature)args.get("feature"));
      String value = Objects.requireNonNull((String)args.get("option"));
      BiConsumer<Player, String> modifier = feature.getModifier();
      if (modifier == null) {
         sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.config.set.error.invalid-key", NamedTextColor.RED));
      } else {
         modifier.accept(target, value);
         sender.sendMessage(
            MessageUtils.translatableWithPrefix(
                  "fakeplayer.command.set.success",
                  Component.text(target.getName(), NamedTextColor.WHITE),
                  Component.translatable(feature, NamedTextColor.GOLD),
                  Component.text(value, NamedTextColor.WHITE)
               )
               .color(NamedTextColor.GRAY)
         );
      }
   }
}
