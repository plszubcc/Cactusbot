package io.github.hello09x.devtools.core.translation;

import java.lang.reflect.UndeclaredThrowableException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TranslatorUtils {

    public TranslatorUtils() {
        super();
    }

    public static @NotNull ClassLoader getDataFolderClassLoader(@NotNull Plugin plugin) {
        URL url;

        try {
            url = plugin.getDataFolder().toURI().toURL();
        } catch (MalformedURLException malformedurlexception) {
            throw new UndeclaredThrowableException(malformedurlexception);
        }

        return new URLClassLoader(new URL[]{url});
    }

    public static @NotNull ClassLoader getJarClassLoader(@NotNull Plugin plugin) {
        return plugin.getClass().getClassLoader();
    }

    public static @Nullable Locale getLocale(@Nullable CommandSender sender) {
        if (sender instanceof Player player) {
            return player.locale();
        } else {
            return null;
        }
    }

    public static @NotNull Locale getDefaultLocale(@NotNull Plugin plugin) {
        FileConfiguration config = plugin.getConfig();
        String locale = config.getString("locale");

        if (locale == null) {
            locale = config.getString("i18n.locale");
        }

        if (locale == null) {
            return Locale.getDefault();
        } else {
            String[] components = locale.split("_");

            if (components.length == 1) {
                return Locale.forLanguageTag(components[0]);
            } else if (components.length == 2) {
                return new Locale(components[0], components[1]);
            } else if (components.length == 3) {
                return new Locale(components[0], components[1], components[2]);
            } else {
                throw new IllegalArgumentException("Invalid locale: " + locale);
            }
        }
    }
}
