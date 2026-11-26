package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class CrystalCommand extends AbstractCommand {
   @Inject
   public CrystalCommand() {
   }

   public void place(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
      Player fake = this.getFakeplayer(sender, args);
      this.bridge.placeCrystal(fake, this.getRange(args));
   }

   public void breakCrystal(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
      Player fake = this.getFakeplayer(sender, args);
      this.bridge.breakCrystal(fake, this.getRange(args));
   }

   private double getRange(@NotNull CommandArguments args) {
      Object value = args.get("range");
      if (value instanceof Number number) {
         return number.doubleValue();
      } else {
         if (value instanceof String text) {
            try {
               return Double.parseDouble(text);
            } catch (NumberFormatException var5) {
            }
         }

         return 4.5;
      }
   }
}
