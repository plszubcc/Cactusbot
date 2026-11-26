package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentException;
import dev.jorel.commandapi.executors.ExecutorType;
import io.github.hello09x.devtools.command.Commands;
import io.github.hello09x.fakeplayer.api.spi.ActionSetting;
import io.github.hello09x.fakeplayer.api.spi.ActionType;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.command.impl.ActionCommand;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.core.repository.model.Feature;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CommandSupports {
   private static final FakeplayerManager manager = (FakeplayerManager)Main.getInjector().getInstance(FakeplayerManager.class);
   private static final FakeplayerConfig config = (FakeplayerConfig)Main.getInjector().getInstance(FakeplayerConfig.class);
   private static final ActionCommand actionCommand = (ActionCommand)Main.getInjector().getInstance(ActionCommand.class);

   public CommandSupports() {
   }

   @NotNull
   public static CommandAPICommand[] newActionCommands(@NotNull ActionType action) {
      return new CommandAPICommand[]{
         (CommandAPICommand)((CommandAPICommand)((CommandAPICommand)Commands.command("once").withShortDescription("fakeplayer.command.action.once"))
               .withOptionalArguments(new Argument[]{fakeplayer("name")}))
            .executes(actionCommand.action(action, ActionSetting.once()), new ExecutorType[0]),
         (CommandAPICommand)((CommandAPICommand)((CommandAPICommand)Commands.command("continuous").withShortDescription("fakeplayer.command.action.continuous"))
               .withOptionalArguments(new Argument[]{fakeplayer("name")}))
            .executes(actionCommand.action(action, ActionSetting.continuous()), new ExecutorType[0]),
         (CommandAPICommand)((CommandAPICommand)((CommandAPICommand)Commands.command("interval").withShortDescription("fakeplayer.command.action.interval"))
               .withOptionalArguments(new Argument[]{Commands.int32("ticks", 1), fakeplayer("name")}))
            .executes((sender, args) -> {
               int interval = (int) args.getOptional("ticks").orElse(1);
               actionCommand.action(sender, args, action, ActionSetting.interval(interval));
            }, new ExecutorType[0]),
         (CommandAPICommand)((CommandAPICommand)((CommandAPICommand)Commands.command("stop").withShortDescription("fakeplayer.command.action.stop"))
               .withOptionalArguments(new Argument[]{fakeplayer("name")}))
            .executes(actionCommand.action(action, ActionSetting.stop()), new ExecutorType[0])
      };
   }

   @NotNull
   public static Argument<Player> fakeplayer(@NotNull String nodeName, @Nullable Predicate<Player> predicate) {
      return (Argument<Player>)new CustomArgument(new StringArgument(nodeName), info -> {
         CommandSender sender = info.sender();
         Player target = sender.isOp() ? manager.get((String)info.currentInput()) : manager.get(sender, (String)info.currentInput());
         if (predicate != null && target != null && !predicate.test(target)) {
            target = null;
         }

         return target;
      }).replaceSuggestions(ArgumentSuggestions.strings(info -> {
         CommandSender sender = (CommandSender)info.sender();
         String arg = info.currentArg();
         List<Player> targets = sender.isOp() ? manager.getAll(predicate) : manager.getAll(sender, predicate);
         Stream<String> names = targets.stream().map(Player::getName);
         if (!arg.isEmpty()) {
            names = names.filter(n -> n.toLowerCase().contains(arg));
         }

         return names.toArray(String[]::new);
      }));
   }

   @NotNull
   public static Argument<Player> fakeplayer(@NotNull String nodeName) {
      return fakeplayer(nodeName, null);
   }

   @NotNull
   public static Argument<List<Player>> fakeplayers(@NotNull String nodeName) {
      return (Argument<List<Player>>)new CustomArgument(new StringArgument(nodeName), info -> {
         CommandSender sender = info.sender();
         String arg = (String)info.currentInput();
         if (arg.equals("-a")) {
            return manager.getAll(sender);
         } else {
            Player target = sender.isOp() ? manager.get(arg) : manager.get(sender, arg);
            return target == null ? Collections.emptyList() : Collections.singletonList(target);
         }
      }).replaceSuggestions(ArgumentSuggestions.strings(info -> {
         CommandSender sender = (CommandSender)info.sender();
         String arg = info.currentArg().toLowerCase();
         List<Player> fakes = sender.isOp() ? manager.getAll() : manager.getAll(sender);
         Stream<String> names = Stream.concat(fakes.stream().map(Player::getName), Stream.of("-a"));
         if (!arg.isEmpty()) {
            names = names.filter(n -> n.toLowerCase().contains(arg));
         }

         return names.toArray(String[]::new);
      }));
   }

   @NotNull
   public static Argument<Feature> configKey(@NotNull String nodeName) {
      return configKey(nodeName, ignored -> true);
   }

   @NotNull
   public static Argument<Feature> configKey(@NotNull String nodeName, @NotNull Predicate<Feature> predicate) {
      return (Argument<Feature>)new CustomArgument(new StringArgument(nodeName), info -> {
         String arg = (String)info.currentInput();

         Feature key;
         try {
            key = Feature.valueOf(arg);
         } catch (Exception var5) {
            throw CustomArgumentException.fromAdventureComponent(Component.translatable("fakeplayer.command.config.set.error.invalid-key"));
         }

         if (!predicate.test(key)) {
            throw CustomArgumentException.fromAdventureComponent(Component.translatable("fakeplayer.command.config.set.error.invalid-key"));
         } else if (!key.testPermissions(info.sender())) {
            throw CustomArgumentException.fromAdventureComponent(Component.translatable("fakeplayer.command.config.set.error.no-permission"));
         } else {
            return key;
         }
      }).replaceSuggestions(ArgumentSuggestions.strings(Arrays.stream(Feature.values()).filter(predicate).map(Enum::name).toArray(String[]::new)));
   }

   @NotNull
   public static Argument<String> configValue(@NotNull String configKeyNodeName, @NotNull String nodeName) {
      return (Argument<String>)new CustomArgument(new StringArgument(nodeName), info -> {
         Feature key = (Feature)info.previousArgs().get(configKeyNodeName);
         if (key == null) {
            throw CustomArgumentException.fromAdventureComponent(Component.translatable("fakeplayer.command.config.set.error.invalid-key"));
         } else {
            String arg = (String)info.currentInput();
            if (!key.getOptions().contains(arg)) {
               throw CustomArgumentException.fromAdventureComponent(Component.translatable("fakeplayer.command.config.set.error.invalid-value"));
            } else {
               return arg;
            }
         }
      }).replaceSuggestions(ArgumentSuggestions.stringCollectionAsync(info -> CompletableFuture.supplyAsync(() -> {
         Feature key = (Feature)info.previousArgs().get(configKeyNodeName);
         if (key == null) {
            return Collections.emptyList();
         } else {
            String arg = info.currentArg().toLowerCase(Locale.ENGLISH);
            return arg.isEmpty() ? key.getOptions() : key.getOptions().stream().filter(option -> option.contains(arg)).toList();
         }
      })));
   }

   public static boolean hasFakeplayerForRespawn(@NotNull CommandSender sender) {
      return config.isKickOnDead() ? false : hasFakeplayer(sender);
   }

   public static boolean needSelect(@NotNull CommandSender sender) {
      return sender.isOp() || config.getPlayerLimit() > 1 && manager.countByCreator(sender) > 0;
   }

   public static boolean hasFakeplayer(@NotNull CommandSender sender) {
      return sender.isOp() || manager.countByCreator(sender) > 0;
   }

   public static boolean isCmdAvailable(@NotNull CommandSender sender) {
      return (sender.hasPermission("cactusbot.command.cmd") || !config.getAllowCommands().isEmpty()) && hasFakeplayer(sender);
   }
}
