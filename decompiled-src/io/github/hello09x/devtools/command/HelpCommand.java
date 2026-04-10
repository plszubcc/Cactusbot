package io.github.hello09x.devtools.command;

import com.google.common.base.Strings;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.executors.ExecutorType;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.ScopedComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HelpCommand {

    private static final Component SEP = Component.text(" - ", NamedTextColor.GRAY);
    private static @Nullable MethodHandle CommandPermission$getPermission;

    public HelpCommand() {
        super();
    }

    private static void generateHelpCommand(@NotNull CommandAPICommand root, @Nullable String fullPrev, boolean translate) {
        List<CommandAPICommand> subCommands = root.getSubcommands();

        if (!subCommands.isEmpty()) {
            String title = " Help: " + root.getName() + " ";
            String prefix = "---------";
            String suffix = Strings.repeat("-", Math.min(50 - title.length() - prefix.length(), prefix.length()));
            TextComponent header = Component.textOfChildren(new ComponentLike[]{Component.text(prefix, NamedTextColor.YELLOW), Component.text(title, NamedTextColor.WHITE), Component.text(suffix, NamedTextColor.YELLOW)});
            List<HelpCommand.Usage> usages = new ArrayList(subCommands.size());

            for (CommandAPICommand command : subCommands) {
                String desc = command.getShortDescription();

                if (desc != null && !desc.isEmpty()) {
                    ScopedComponent description = (ScopedComponent) (translate ? Component.translatable(desc, NamedTextColor.WHITE) : Component.text(desc, NamedTextColor.WHITE));

                    usages.add(new HelpCommand.Usage(command, getPermissionString(command.getPermission()), Component.textOfChildren(new ComponentLike[]{Component.text(command.getName(), NamedTextColor.GOLD), HelpCommand.SEP, description})));
                }
            }

            root.withSubcommand((CommandAPICommand) ((CommandAPICommand) ((CommandAPICommand) (new CommandAPICommand("help")).withAliases(new String[]{"?"})).withOptionalArguments(new Argument[]{new IntegerArgument("page", 1), new IntegerArgument("size", 1)})).executes((senderx, argsx) -> {
                int page = (Integer) argsx.getOptional("page").orElse(1);
                int size = (Integer) argsx.getOptional("size").orElse(10);

                if (!usages.isEmpty()) {
                    Page<HelpCommand.Usage> content = Page.<HelpCommand.Usage>of(usages.stream().filter((usagex) -> {
                        return usagex.permission == null ? true : senderx.hasPermission(usagex.permission);
                    }).toList(), page, size);
                    String current = fullPrev != null ? fullPrev + " " + root.getName() : root.getName();
                    Component message = content.asComponent(header, (usagex) -> {
                        TextComponent c = Component.textOfChildren(new ComponentLike[]{Component.text("- "), usagex.component()});

                        if (!usagex.command.getSubcommands().isEmpty()) {
                            c = (TextComponent) c.clickEvent(ClickEvent.runCommand("/%s ? 1 %d".formatted(current + " " + usagex.command.getName(), size)));
                        } else {
                            c = (TextComponent) c.clickEvent(ClickEvent.suggestCommand("/%s".formatted(current + " " + usagex.command.getName())));
                        }

                        return c;
                    }, (ix) -> {
                        return "/%s ? %d %d".formatted(current, ix, size);
                    });

                    senderx.sendMessage(message);
                }
            }, new ExecutorType[0]));

            for (CommandAPICommand cmd : subCommands) {
                generateHelpCommand(cmd, fullPrev == null ? root.getName() : fullPrev + " " + root.getName(), translate);
            }

        }
    }

    public static void generateHelpCommand(@NotNull CommandAPICommand root, boolean translate) {
        generateHelpCommand(root, (String) null, translate);
    }

    private static @Nullable String getPermissionString(@NotNull CommandPermission permission) {
        if (permission == CommandPermission.OP) {
            return "minecraft.command.op";
        } else if (permission == CommandPermission.NONE) {
            return null;
        } else if (HelpCommand.CommandPermission$getPermission == null) {
            return null;
        } else {
            try {
                return (String) HelpCommand.CommandPermission$getPermission.invoke(permission).orElse((Object) null);
            } catch (Throwable throwable) {
                return null;
            }
        }
    }

    static {
        try {
            HelpCommand.CommandPermission$getPermission = MethodHandles.lookup().findVirtual(CommandPermission.class, "getPermission", MethodType.methodType(Optional.class));
        } catch (IllegalAccessException | NoSuchMethodException nosuchmethodexception) {
            HelpCommand.CommandPermission$getPermission = null;
        }

    }

    public static record Usage(@NotNull CommandAPICommand command, @Nullable String permission, @NotNull Component component) {

        public Usage {
            super();
        }
    }
}
