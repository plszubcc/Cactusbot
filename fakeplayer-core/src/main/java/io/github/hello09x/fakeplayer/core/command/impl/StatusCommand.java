package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.core.utils.ExperienceUtils;
import io.github.hello09x.fakeplayer.core.repository.model.Feature;
import io.github.hello09x.fakeplayer.core.util.Attributes;
import io.github.hello09x.fakeplayer.core.util.Mth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class StatusCommand extends AbstractCommand {
   private static final Component LINE_SPLITTER = Component.text(StringUtils.repeat("-", 20), NamedTextColor.GRAY);

   public StatusCommand() {
   }

   @NotNull
   private static NamedTextColor color(double current, double max) {
      double p = current / max;
      NamedTextColor color;
      if (p >= 0.75) {
         color = NamedTextColor.GREEN;
      } else if (p >= 0.5) {
         color = NamedTextColor.YELLOW;
      } else if (p >= 0.25) {
         color = NamedTextColor.GOLD;
      } else if (p >= 0.125) {
         color = NamedTextColor.RED;
      } else {
         color = NamedTextColor.DARK_RED;
      }

      return color;
   }

   public void status(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
      Player fake = super.getFakeplayer(sender, args);
      TranslatableComponent title = (TranslatableComponent)Component.translatable(
            "fakeplayer.command.status.title", new ComponentLike[]{Component.text(fake.getName(), NamedTextColor.WHITE)}
         )
         .color(NamedTextColor.GRAY);
      ArrayList<Component> lines = new ArrayList<>(6);
      lines.add(title);
      lines.add(this.getHealthLine(fake));
      lines.add(this.getFoodLine(fake));
      if (sender.hasPermission("cactusbot.command.expme")) {
         lines.add(this.getExperienceLine(fake));
      }

      lines.add(LINE_SPLITTER);
      lines.add(this.getFeatureLine(fake));
      sender.sendMessage(Component.join(JoinConfiguration.newlines(), lines));
   }

   @NotNull
   private Component getFoodLine(@NotNull Player target) {
      int food = target.getFoodLevel();
      double max = 20.0;
      return Component.translatable(
            "fakeplayer.command.status.food",
            new ComponentLike[]{
               Component.textOfChildren(
                  new ComponentLike[]{
                     Component.text(Mth.floor(food, 0.5), color(food, max)),
                     Component.text("/", NamedTextColor.GRAY),
                     Component.text(max, NamedTextColor.WHITE)
                  }
               )
            }
         )
         .color(NamedTextColor.WHITE);
   }

   @NotNull
   private Component getHealthLine(@NotNull Player target) {
      double health = target.getHealth();
      double max = Optional.ofNullable(target.getAttribute(Attributes.maxHealth())).<Double>map(AttributeInstance::getValue).orElse(20.0);
      return Component.translatable(
         "fakeplayer.command.status.health",
         new ComponentLike[]{
            Component.textOfChildren(
                  new ComponentLike[]{
                     Component.text(Mth.floor(health, 0.5), color(health, max)),
                     Component.text("/", NamedTextColor.GRAY),
                     Component.text(max, NamedTextColor.WHITE)
                  }
               )
               .color(NamedTextColor.WHITE)
         }
      );
   }

   @NotNull
   private Component getExperienceLine(@NotNull Player target) {
      int level = target.getLevel();
      int points = ExperienceUtils.getExp(target);
      return Component.textOfChildren(
         new ComponentLike[]{
            Component.translatable(
                  "fakeplayer.command.status.exp",
                  new ComponentLike[]{Component.text(level, NamedTextColor.GREEN), Component.text(points, NamedTextColor.GREEN)}
               )
               .color(NamedTextColor.WHITE),
            Component.space(),
            Component.translatable("fakeplayer.command.status.exp.withdraw", NamedTextColor.AQUA)
               .clickEvent(ClickEvent.runCommand("/cb expme " + target.getName()))
         }
      );
   }

   @NotNull
   private Component getFeatureLine(@NotNull Player faker) {
      ArrayList<Component> messages = new ArrayList<>();

      for (Feature feature : Feature.values()) {
         Function<Player, String> detector = feature.getDetector();
         if (detector != null) {
            TranslatableComponent name = Component.translatable(feature, NamedTextColor.WHITE);
            List<String> options = feature.getOptions();
            String status = detector.apply(faker);
            messages.add(
               Component.textOfChildren(
                  new ComponentLike[]{
                     name,
                     Component.space(),
                     Component.join(
                        JoinConfiguration.separator(Component.space()),
                        options.stream()
                           .map(
                              option -> {
                                 Style style = option.equals(status)
                                    ? Style.style(NamedTextColor.GREEN, new TextDecoration[]{TextDecoration.UNDERLINED})
                                    : Style.style(NamedTextColor.GRAY);
                                 return (TextComponent)((TextComponent)Component.text("[" + option + "]").style(style))
                                    .clickEvent(ClickEvent.runCommand("/cb set %s %s %s".formatted(feature.name(), option, faker.getName())));
                              }
                           )
                           .toList()
                     )
                  }
               )
            );
         }
      }

      return Component.join(JoinConfiguration.newlines(), messages);
   }
}
