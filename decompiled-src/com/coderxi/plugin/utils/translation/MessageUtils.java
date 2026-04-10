package com.coderxi.plugin.utils.translation;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class MessageUtils {

    private static Component PREFIX;

    public MessageUtils() {
        super();
    }

    public static void init(JavaPlugin plugin) {
        String prefix = plugin.getConfig().getString("i18n.message_prefix");

        if (prefix == null) {
            MessageUtils.PREFIX = Component.text("");
        } else {
            MessageUtils.PREFIX = LegacyComponentSerializer.legacyAmpersand().deserialize(prefix);
        }

    }

    public static Component translatableWithPrefix(@NotNull String key) {
        return MessageUtils.PREFIX.append(Component.translatable(key));
    }

    public static Component translatableWithPrefix(@NotNull String key, @NotNull TextColor color) {
        return MessageUtils.PREFIX.append(Component.translatable(key, color));
    }

    public static Component translatableWithPrefix(@NotNull String key, @NotNull ComponentLike... args) {
        return MessageUtils.PREFIX.append(Component.translatable(key, Style.empty(), args));
    }
}
