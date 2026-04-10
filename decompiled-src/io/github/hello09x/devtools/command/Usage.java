package io.github.hello09x.devtools.command;

import java.util.function.Predicate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Usage {

    private final @NotNull Component component;
    private @Nullable String permission;
    private @Nullable Predicate<CommandSender> requirement;

    public static @NotNull Usage of(@NotNull String usage, @NotNull String description, @Nullable String permission, @Nullable Predicate<CommandSender> requirement) {
        return new Usage(Component.text("§6" + usage + " §7- §f" + description), permission, requirement);
    }

    public static @NotNull Usage of(@NotNull String usage, @NotNull ComponentLike description, @Nullable String permission, @Nullable Predicate<CommandSender> requirement) {
        return new Usage(Component.textOfChildren(new ComponentLike[]{Component.text(usage, NamedTextColor.GOLD), Component.text(" - ", NamedTextColor.GRAY), description.asComponent().color(NamedTextColor.WHITE)}), permission, requirement);
    }

    public static @NotNull Usage of(@NotNull String usage, @NotNull String description, @Nullable String permission) {
        return of(usage, description, permission, (Predicate) null);
    }

    public static @NotNull Usage of(@NotNull String usage, @NotNull String description) {
        return of(usage, description, (String) null);
    }

    public static @NotNull Usage of(@NotNull String usage, @NotNull ComponentLike description) {
        return of(usage, description, (String) null);
    }

    public static @NotNull Usage of(@NotNull String usage, @NotNull ComponentLike description, @Nullable String permission) {
        return of(usage, description, permission, (Predicate) null);
    }

    public @NotNull Component asComponent() {
        return this.component;
    }

    public @Nullable String permission() {
        return this.permission;
    }

    public @Nullable Predicate<CommandSender> requirement() {
        return this.requirement;
    }

    private Usage(@NotNull Component component, @Nullable String permission, @Nullable Predicate<CommandSender> requirement) {
        this.component = component;
        this.permission = permission;
        this.requirement = requirement;
    }
}
