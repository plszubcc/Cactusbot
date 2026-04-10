package io.github.hello09x.devtools.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CommandArgument;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentException;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument.OnePlayer;
import dev.jorel.commandapi.arguments.FloatArgument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.IntegerRangeArgument;
import dev.jorel.commandapi.arguments.ItemStackArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LongArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.arguments.PlayerProfileArgument;
import dev.jorel.commandapi.arguments.RotationArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import dev.jorel.commandapi.arguments.UUIDArgument;
import dev.jorel.commandapi.arguments.WorldArgument;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Commands {

    public Commands() {
        super();
    }

    public static @NotNull CommandAPICommand command(@NotNull String name) {
        return new CommandAPICommand(name);
    }

    public static @NotNull CommandTree commandTree(@NotNull String name) {
        return new CommandTree(name);
    }

    public static @NotNull IntegerArgument int32(@NotNull String name) {
        return new IntegerArgument(name);
    }

    public static @NotNull IntegerArgument int32(@NotNull String name, int min) {
        return new IntegerArgument(name, min);
    }

    public static @NotNull IntegerArgument int32(@NotNull String name, int min, int max) {
        return new IntegerArgument(name, min, max);
    }

    public static @NotNull LongArgument int64(@NotNull String name) {
        return new LongArgument(name);
    }

    public static @NotNull LongArgument int64(@NotNull String name, long min) {
        return new LongArgument(name, min);
    }

    public static @NotNull LongArgument int64(@NotNull String name, long min, long max) {
        return new LongArgument(name, min, max);
    }

    public static @NotNull FloatArgument float32(@NotNull String name) {
        return new FloatArgument(name);
    }

    public static @NotNull FloatArgument float32(@NotNull String name, float min) {
        return new FloatArgument(name, min);
    }

    public static @NotNull FloatArgument float32(@NotNull String name, float min, float max) {
        return new FloatArgument(name, min, max);
    }

    public static @NotNull DoubleArgument float64(@NotNull String name) {
        return new DoubleArgument(name);
    }

    public static @NotNull DoubleArgument float64(@NotNull String name, double min) {
        return new DoubleArgument(name, min);
    }

    public static @NotNull DoubleArgument float64(@NotNull String name, double min, double max) {
        return new DoubleArgument(name, min, max);
    }

    public static @NotNull MultiLiteralArgument literals(@NotNull String name, @NotNull List<String> literals) {
        return new MultiLiteralArgument(name, (String[]) literals.toArray((x$0x) -> {
            return new String[x$0x];
        }));
    }

    public static @NotNull LiteralArgument literal(@NotNull String literal) {
        return new LiteralArgument(literal);
    }

    public static @NotNull LiteralArgument literal(@NotNull String name, @NotNull String literal) {
        return new LiteralArgument(name, literal);
    }

    public static @NotNull CommandArgument cmd(@NotNull String name) {
        return new CommandArgument(name);
    }

    public static <E extends Enum<E>> @NotNull Argument<E> enumerate(@NotNull String name, Class<E> enumClass) {
        return (Argument) (new CustomArgument(new StringArgument(name), (infox) -> {
            try {
                E value = (E) Enum.valueOf(enumClass, infox.input());

                return value;
            } catch (IllegalArgumentException illegalargumentexception) {
                throw CustomArgumentException.fromString("Invalid value: " + infox.input());
            }
        })).replaceSuggestions(ArgumentSuggestions.strings((String[]) Arrays.stream((Enum[]) enumClass.getEnumConstants()).map(Enum::name).toArray((x$0x) -> {
            return new String[x$0x];
        })));
    }

    public static @NotNull Argument<File> file(@NotNull File folder, @NotNull String name, @Nullable String suffix) {
        return (Argument) (new CustomArgument(new GreedyStringArgument(name), (infox) -> {
            String filename = infox.input();
            File file = new File(folder, filename);

            if (!file.exists()) {
                throw CustomArgumentException.fromString("File not found: " + file.getPath());
            } else if (suffix != null && !file.getName().endsWith(suffix)) {
                throw CustomArgumentException.fromString("Unsupported file: " + file.getPath());
            } else {
                return file;
            }
        })).replaceSuggestions(ArgumentSuggestions.strings((infox) -> {
            String arg = infox.currentArg();

            return (String[]) Arrays.stream((File[]) Optional.ofNullable(folder.listFiles((dirx, filex) -> {
                return filex.contains(arg) && (suffix == null || filex.endsWith(suffix));
            })).orElse(new File[0])).map(File::getName).toArray((x$0x) -> {
                return new String[x$0x];
            });
        }));
    }

    public static @NotNull LocationArgument location(@NotNull String name) {
        return new LocationArgument(name);
    }

    public static @NotNull RotationArgument rotation(@NotNull String name) {
        return new RotationArgument(name);
    }

    public static @NotNull WorldArgument world(@NotNull String name) {
        return new WorldArgument(name);
    }

    public static @NotNull TextArgument text(@NotNull String name) {
        return new TextArgument(name);
    }

    public static @NotNull PlayerProfileArgument playerProfile(@NotNull String name) {
        return new PlayerProfileArgument(name);
    }

    public static OnePlayer player(@NotNull String name) {
        return new OnePlayer(name);
    }

    public static @NotNull Argument<OfflinePlayer> offlinePlayer(@NotNull String name) {
        return (Argument) (new CustomArgument(new StringArgument(name), (infox) -> {
            String playerName = (String) infox.currentInput();
            OfflinePlayer player = Bukkit.getPlayerExact(playerName);

            if (player == null) {
                try {
                    player = Bukkit.getOfflinePlayer(playerName);
                } catch (Exception exception) {
                    throw CustomArgumentException.fromString("Player not found: " + playerName);
                }
            }

            return player;
        })).replaceSuggestions(ArgumentSuggestions.strings((infox) -> {
            String input = infox.currentArg().toLowerCase(Locale.ENGLISH);

            return (String[]) Bukkit.getOnlinePlayers().stream().map(Player::getName).filter((nx) -> {
                return nx.toLowerCase(Locale.ENGLISH).startsWith(input);
            }).toArray((x$0x) -> {
                return new String[x$0x];
            });
        }));
    }

    public static @NotNull UUIDArgument uuid(@NotNull String name) {
        return new UUIDArgument(name);
    }

    public static @NotNull IntegerRangeArgument integerRange(@NotNull String name) {
        return new IntegerRangeArgument(name);
    }

    public static @NotNull String usage(@NotNull String command, @NotNull String description) {
        return "§6" + command + " §7- §f" + description;
    }

    public static @NotNull GreedyStringArgument strings(@NotNull String name) {
        return new GreedyStringArgument(name);
    }

    public static @NotNull ItemStackArgument itemStack(@NotNull String name) {
        return new ItemStackArgument(name);
    }
}
