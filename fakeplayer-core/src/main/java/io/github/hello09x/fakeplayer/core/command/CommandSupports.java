package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentException;
import dev.jorel.commandapi.arguments.StringArgument;
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

    private static final FakeplayerManager manager = (FakeplayerManager) Main.getInjector().getInstance(FakeplayerManager.class);
    private static final FakeplayerConfig config = (FakeplayerConfig) Main.getInjector().getInstance(FakeplayerConfig.class);
    private static final ActionCommand actionCommand = (ActionCommand) Main.getInjector().getInstance(ActionCommand.class);

    public CommandSupports() {
        super();
    }

    public static @NotNull CommandAPICommand[] newActionCommands(@NotNull ActionType action) {
        return new CommandAPICommand[]{(CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("once").withShortDescription("fakeplayer.command.action.once")).withOptionalArguments(new Argument[]{fakeplayer("name")})).executes(CommandSupports.actionCommand.action(action, ActionSetting.once()), new ExecutorType[0]), (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("continuous").withShortDescription("fakeplayer.command.action.continuous")).withOptionalArguments(new Argument[]{fakeplayer("name")})).executes(CommandSupports.actionCommand.action(action, ActionSetting.continuous()), new ExecutorType[0]), (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("interval").withShortDescription("fakeplayer.command.action.interval")).withOptionalArguments(new Argument[]{Commands.int32("ticks", 1), fakeplayer("name")})).executes((senderx, argsx) -> {
                    int interval = (Integer) argsx.getOptional("ticks").orElse(1);

                    CommandSupports.actionCommand.action(senderx, argsx, action, ActionSetting.interval(interval));
                }, new ExecutorType[0]), (CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) Commands.command("stop").withShortDescription("fakeplayer.command.action.stop")).withOptionalArguments(new Argument[]{fakeplayer("name")})).executes(CommandSupports.actionCommand.action(action, ActionSetting.stop()), new ExecutorType[0])};
    }

    public static @NotNull Argument<Player> fakeplayer(@NotNull String nodeName, @Nullable Predicate<Player> predicate) {
        return (Argument) (new CustomArgument(new StringArgument(nodeName), (infox) -> {
            CommandSender sender = infox.sender();
            Player target = sender.isOp() ? CommandSupports.manager.get((String) infox.currentInput()) : CommandSupports.manager.get(sender, (String) infox.currentInput());

            if (predicate != null && target != null && !predicate.test(target)) {
                target = null;
            }

            return target;
        })).replaceSuggestions(ArgumentSuggestions.strings((infox) -> {
            CommandSender sender = (CommandSender) infox.sender();
            String arg = infox.currentArg();
            List<Player> targets = sender.isOp() ? CommandSupports.manager.getAll(predicate) : CommandSupports.manager.getAll(sender, predicate);
            Stream<String> names = targets.stream().map(Player::getName);

            if (!arg.isEmpty()) {
                names = names.filter((nx) -> {
                    return nx.toLowerCase().contains(arg);
                });
            }

            return (String[]) names.toArray((x$0x) -> {
                return new String[x$0x];
            });
        }));
    }

    public static @NotNull Argument<Player> fakeplayer(@NotNull String nodeName) {
        return fakeplayer(nodeName, (Predicate) null);
    }

    public static @NotNull Argument<List<Player>> fakeplayers(@NotNull String nodeName) {
        return (Argument) (new CustomArgument(new StringArgument(nodeName), (infox) -> {
            CommandSender sender = infox.sender();
            String arg = (String) infox.currentInput();

            if (arg.equals("-a")) {
                return CommandSupports.manager.getAll(sender);
            } else {
                Player target = sender.isOp() ? CommandSupports.manager.get(arg) : CommandSupports.manager.get(sender, arg);

                return target == null ? Collections.emptyList() : Collections.singletonList(target);
            }
        })).replaceSuggestions(ArgumentSuggestions.strings((infox) -> {
            CommandSender sender = (CommandSender) infox.sender();
            String arg = infox.currentArg().toLowerCase();
            List<Player> fakes = sender.isOp() ? CommandSupports.manager.getAll() : CommandSupports.manager.getAll(sender);
            Stream<String> names = Stream.concat(fakes.stream().map(Player::getName), Stream.of("-a"));

            if (!arg.isEmpty()) {
                names = names.filter((nx) -> {
                    return nx.toLowerCase().contains(arg);
                });
            }

            return (String[]) names.toArray((x$0x) -> {
                return new String[x$0x];
            });
        }));
    }

    public static @NotNull Argument<Feature> configKey(@NotNull String nodeName) {
        return configKey(nodeName, (ignoredx) -> {
            return true;
        });
    }

    public static @NotNull Argument<Feature> configKey(@NotNull String nodeName, @NotNull Predicate<Feature> predicate) {
        return (Argument) (new CustomArgument(new StringArgument(nodeName), (infox) -> {
            String arg = (String) infox.currentInput();

            Feature key;

            try {
                key = Feature.valueOf(arg);
            } catch (Exception exception) {
                throw CustomArgumentException.fromAdventureComponent(Component.translatable("fakeplayer.command.config.set.error.invalid-key"));
            }

            if (!predicate.test(key)) {
                throw CustomArgumentException.fromAdventureComponent(Component.translatable("fakeplayer.command.config.set.error.invalid-key"));
            } else if (!key.testPermissions(infox.sender())) {
                throw CustomArgumentException.fromAdventureComponent(Component.translatable("fakeplayer.command.config.set.error.no-permission"));
            } else {
                return key;
            }
        })).replaceSuggestions(ArgumentSuggestions.strings((String[]) Arrays.stream(Feature.values()).filter(predicate).map(Enum::name).toArray((x$0x) -> {
            return new String[x$0x];
        })));
    }

    public static @NotNull Argument<String> configValue(@NotNull String configKeyNodeName, @NotNull String nodeName) {
        return (Argument) (new CustomArgument(new StringArgument(nodeName), (infox) -> {
            Feature key = (Feature) infox.previousArgs().get(configKeyNodeName);

            if (key == null) {
                throw CustomArgumentException.fromAdventureComponent(Component.translatable("fakeplayer.command.config.set.error.invalid-key"));
            } else {
                String arg = (String) infox.currentInput();

                if (!key.getOptions().contains(arg)) {
                    throw CustomArgumentException.fromAdventureComponent(Component.translatable("fakeplayer.command.config.set.error.invalid-value"));
                } else {
                    return arg;
                }
            }
        })).replaceSuggestions(ArgumentSuggestions.stringCollectionAsync((infox) -> {
            return CompletableFuture.supplyAsync(() -> {
                Feature key = (Feature) infox.previousArgs().get(configKeyNodeName);

                if (key == null) {
                    return Collections.emptyList();
                } else {
                    String arg = infox.currentArg().toLowerCase(Locale.ENGLISH);

                    return arg.isEmpty() ? key.getOptions() : key.getOptions().stream().filter((optionx) -> {
                        return optionx.contains(arg);
                    }).toList();
                }
            });
        }));
    }

    public static boolean hasFakeplayerForRespawn(@NotNull CommandSender sender) {
        return CommandSupports.config.isKickOnDead() ? false : hasFakeplayer(sender);
    }

    public static boolean needSelect(@NotNull CommandSender sender) {
        return sender.isOp() || CommandSupports.config.getPlayerLimit() > 1 && CommandSupports.manager.countByCreator(sender) > 0;
    }

    public static boolean hasFakeplayer(@NotNull CommandSender sender) {
        return sender.isOp() || CommandSupports.manager.countByCreator(sender) > 0;
    }

    public static boolean isCmdAvailable(@NotNull CommandSender sender) {
        return (sender.hasPermission("cactusbot.command.cmd") || !CommandSupports.config.getAllowCommands().isEmpty()) && hasFakeplayer(sender);
    }
}
