package io.github.hello09x.fakeplayer.core.command.impl;

import com.coderxi.plugin.utils.translation.MessageUtils;
import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.util.Mth;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

@Singleton
public class DistanceCommand extends AbstractCommand {
   public DistanceCommand() {
   }

   public void distance(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
      Player fake = super.getFakeplayer(sender, args);
      Location from = fake.getLocation().toBlockLocation();
      Location to = sender.getLocation().toBlockLocation();
      if (!Objects.equals(from.getWorld(), to.getWorld())) {
         sender.sendMessage(
            MessageUtils.translatableWithPrefix("fakeplayer.command.distance.error.too-far", Component.text(fake.getName(), NamedTextColor.WHITE))
         );
      } else {
         double euclidean = Mth.floor(from.distance(to), 0.5);
         int x = Math.abs(from.getBlockX() - to.getBlockX());
         int y = Math.abs(from.getBlockY() - to.getBlockY());
         int z = Math.abs(from.getBlockZ() - to.getBlockZ());
         sender.sendMessage(
            Component.textOfChildren(
               new ComponentLike[]{
                  Component.translatable("fakeplayer.command.distance.title", new ComponentLike[]{Component.text(fake.getName(), NamedTextColor.WHITE)})
                     .color(NamedTextColor.GRAY),
                  Component.newline(),
                  Component.translatable("fakeplayer.command.distance.euclidean", NamedTextColor.GRAY),
                  Component.space(),
                  Component.text(euclidean, NamedTextColor.WHITE),
                  Component.newline(),
                  Component.translatable("fakeplayer.command.distance.x", NamedTextColor.GRAY),
                  Component.space(),
                  Component.text(x, NamedTextColor.WHITE),
                  Component.newline(),
                  Component.translatable("fakeplayer.command.distance.y", NamedTextColor.GRAY),
                  Component.space(),
                  Component.text(y, NamedTextColor.WHITE),
                  Component.newline(),
                  Component.translatable("fakeplayer.command.distance.z", NamedTextColor.GRAY),
                  Component.space(),
                  Component.text(z, NamedTextColor.WHITE)
               }
            )
         );
      }
   }
}
