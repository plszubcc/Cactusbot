package io.github.hello09x.fakeplayer.core.command.impl;

import com.coderxi.plugin.utils.translation.MessageUtils;
import com.google.inject.Singleton;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.util.SkinRestorerCompat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@Singleton
public class SkinCommand extends AbstractCommand {
   public SkinCommand() {
   }

   public void skin(@NotNull CommandSender sender, @NotNull CommandArguments args) {
      if (!SkinRestorerCompat.isAvailable()) {
         Plugin sr = Bukkit.getPluginManager().getPlugin("SkinsRestorer");
         if (sr != null) {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.skin.error.skinsrestorer-hook-failed").color(NamedTextColor.RED));
         } else {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.skin.error.skinsrestorer-missing").color(NamedTextColor.RED));
         }
      } else {
         Player fake = (Player)args.get("name");
         String skin = ((String)args.get("skin")).trim();
         if (skin.isBlank()) {
            sender.sendMessage(MessageUtils.translatableWithPrefix("fakeplayer.command.skin.error.invalid-skin").color(NamedTextColor.RED));
         } else {
            String variant = args.getOptional("variant").map(Object::toString).orElse(null);
            this.apply(fake, skin, variant, 0L);
            this.apply(fake, skin, variant, 20L);
            sender.sendMessage(
               MessageUtils.translatableWithPrefix(
                     "fakeplayer.command.skin.success", Component.text(fake.getName(), NamedTextColor.WHITE), Component.text(skin, NamedTextColor.WHITE)
                  )
                  .color(NamedTextColor.GRAY)
            );
         }
      }
   }

   private void apply(@NotNull Player fake, @NotNull String skin, String variant, long delayTicks) {
      Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
         if (fake.isOnline() && !this.manager.isNotFake(fake)) {
            SkinRestorerCompat.applySkinByInputAsync(Main.getInstance(), fake, skin, variant);
         }
      }, delayTicks);
   }
}
