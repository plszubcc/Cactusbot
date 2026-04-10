package io.github.hello09x.fakeplayer.core.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.SuggestionInfo;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CommandAPIArgumentType;
import dev.jorel.commandapi.arguments.GreedyArgument;
import dev.jorel.commandapi.arguments.SuggestionsBranch;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.wrappers.CommandResult;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FakePlayerCommandArgument extends Argument<CommandResult> implements GreedyArgument {

    SuggestionsBranch<CommandSender> replacements = SuggestionsBranch.suggest(new ArgumentSuggestions[0]);

    public FakePlayerCommandArgument(String nodeName) {
        super(nodeName, StringArgumentType.greedyString());
        this.applySuggestions();
    }

    private void applySuggestions() {
        super.replaceSuggestions((infox, builderx) -> {
            Player sender = this.getTarget(infox);

            if (sender == null) {
                return Suggestions.empty();
            } else {
                SimpleCommandMap commandMap = CommandAPIBukkit.get().getNMS().getSimpleCommandMap();
                String command = infox.currentArg();
                StringReader context = new StringReader(command);

                if (!command.contains(" ")) {
                    ArgumentSuggestions<CommandSender> replacement = this.replacements.getNextSuggestion(sender, new String[0]);

                    if (replacement != null) {
                        return replacement.suggest(new SuggestionInfo(sender, new CommandArguments(new Object[0], new LinkedHashMap(), new String[0], new LinkedHashMap(), infox.currentInput()), command, command), builderx);
                    } else {
                        List<String> results = commandMap.tabComplete(sender, command);

                        if (results == null) {
                            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(context);
                        } else {
                            if (sender instanceof Player) {
                                for (String result : results) {
                                    builderx.suggest(result.substring(1));
                                }
                            } else {
                                for (String result : results) {
                                    builderx.suggest(result);
                                }
                            }

                            return builderx.buildFuture();
                        }
                    }
                } else {
                    String commandLabel = command.substring(0, command.indexOf(" "));
                    Command target = commandMap.getCommand(commandLabel);

                    if (target == null) {
                        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(context);
                    } else {
                        String[] arguments = command.split(" ");

                        if (!arguments[0].isEmpty() && command.endsWith(" ")) {
                            arguments = (String[]) Arrays.copyOf(arguments, arguments.length + 1);
                            arguments[arguments.length - 1] = "";
                        }

                        builderx = builderx.createOffset(builderx.getStart() + command.lastIndexOf(" ") + 1);
                        int lastIndex = arguments.length - 1;
                        String[] previousArguments = (String[]) Arrays.copyOf(arguments, lastIndex);
                        ArgumentSuggestions<CommandSender> replacement = this.replacements.getNextSuggestion(sender, previousArguments);

                        if (replacement != null) {
                            return replacement.suggest(new SuggestionInfo(sender, new CommandArguments(previousArguments, new LinkedHashMap(), previousArguments, new LinkedHashMap(), infox.currentInput()), command, arguments[lastIndex]), builderx);
                        } else {
                            arguments = (String[]) Arrays.copyOfRange(arguments, 1, arguments.length);
                            Location location = null;

                            if (sender instanceof Player) {
                                Block block = sender.getTargetBlockExact(5, FluidCollisionMode.NEVER);

                                if (block != null) {
                                    location = block.getLocation();
                                }
                            }

                            for (String tabCompletion : target.tabComplete(sender, commandLabel, arguments, location)) {
                                builderx.suggest(tabCompletion);
                            }

                            return builderx.buildFuture();
                        }
                    }
                }
            }
        });
    }

    @SafeVarargs
    public final FakePlayerCommandArgument replaceSuggestions(ArgumentSuggestions<CommandSender>... suggestions) {
        this.replacements = SuggestionsBranch.suggest(suggestions);
        return this;
    }

    public FakePlayerCommandArgument replaceSuggestions(ArgumentSuggestions<CommandSender> suggestions) {
        this.replacements = SuggestionsBranch.suggest(new ArgumentSuggestions[]{suggestions});
        return this;
    }

    @SafeVarargs
    public final Argument<CommandResult> branchSuggestions(SuggestionsBranch<CommandSender>... branches) {
        this.replacements.branch(branches);
        return this;
    }

    public Class<CommandResult> getPrimitiveType() {
        return CommandResult.class;
    }

    public CommandAPIArgumentType getArgumentType() {
        return CommandAPIArgumentType.COMMAND;
    }

    public <CommandSourceStack> CommandResult parseArgument(CommandContext<CommandSourceStack> cmdCtx, String key, CommandArguments previousArgs) throws CommandSyntaxException {
        String command = (String) cmdCtx.getArgument(key, String.class);
        SimpleCommandMap commandMap = CommandAPIBukkit.get().getNMS().getSimpleCommandMap();
        StringReader context = new StringReader(command);
        CommandSender sender = (CommandSender) CommandAPIBukkit.get().getSenderForCommand(cmdCtx, false).getSource();
        Player target = (Player) previousArgs.get("name");

        if (target == null) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(context);
        } else {
            FakeplayerManager manager = (FakeplayerManager) Main.getInjector().getInstance(FakeplayerManager.class);

            if (sender.isOp()) {
                if (manager.isNotFake(target)) {
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(context);
                }
            } else if (manager.get(sender, target.getName()) == null) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(context);
            }

            String[] arguments = command.split(" ");
            String commandLabel = arguments[0];
            Command cmd = commandMap.getCommand(commandLabel);

            if (cmd == null) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(context);
            } else {
                this.replacements.enforceReplacements(target, arguments);
                return new CommandResult(cmd, (String[]) Arrays.copyOfRange(arguments, 1, arguments.length));
            }
        }
    }

    private @Nullable Player getTarget(@NotNull SuggestionInfo<CommandSender> info) {
        String[] args = info.previousArgs().fullInput().split(" ");

        if (args.length < 3) {
            return null;
        } else {
            String name = args[2];
            FakeplayerManager manager = (FakeplayerManager) Main.getInjector().getInstance(FakeplayerManager.class);

            return ((CommandSender) info.sender()).isOp() ? manager.get(name) : manager.get((CommandSender) info.sender(), name);
        }
    }
}
