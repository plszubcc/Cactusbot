package io.github.hello09x.devtools.core.utils;

import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ComponentUtils {

    private static final PlainTextComponentSerializer stringer = PlainTextComponentSerializer.plainText();
    private static final MiniMessage formatter = MiniMessage.miniMessage();

    public ComponentUtils() {
        super();
    }

    public static @NotNull String toString(@NotNull Component component) {
        return ComponentUtils.stringer.serialize(component);
    }

    public static @NotNull String toString(@NotNull Component component, @Nullable Locale locale) {
        return ComponentUtils.stringer.serialize(GlobalTranslator.render(component, locale == null ? Locale.getDefault() : locale));
    }

    public static @NotNull Component join(@NotNull ComponentLike sep, @NotNull ComponentLike... components) {
        return Component.join(JoinConfiguration.separator(sep), components);
    }

    public static @NotNull Component join(@NotNull ComponentLike sep, @NotNull Iterable<? extends ComponentLike> components) {
        return Component.join(JoinConfiguration.separator(sep), components);
    }

    public static @NotNull Component noItalic(@NotNull Component component) {
        return component.decoration(TextDecoration.ITALIC, false);
    }
}
