package io.github.hello09x.devtools.core.translation;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.renderer.TranslatableComponentRenderer;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.translation.Translator;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PluginTranslator implements Translator {

    private final TranslationConfig config;
    private final Plugin plugin;
    private TranslationRegistry registry;
    private final ClassLoader[] classLoaders;
    private final Set<Locale> loadedLocales = new HashSet();
    private final TranslatableComponentRenderer<Locale> renderer = new PluginTranslatorRenderer(this);

    private PluginTranslator(@NotNull Plugin plugin, @NotNull TranslationConfig config, @NotNull TranslationRegistry registry, @NotNull ClassLoader[] classLoaders) {
        super();
        this.plugin = plugin;
        this.config = config;
        this.registry = registry;
        this.classLoaders = classLoaders;
    }

    public static PluginTranslator of(@NotNull Plugin plugin, @NotNull TranslationConfig config) {
        TranslationRegistry registry = createRegistry(plugin, config);
        ClassLoader[] classLoaders = new ClassLoader[]{TranslatorUtils.getDataFolderClassLoader(plugin), TranslatorUtils.getJarClassLoader(plugin)};

        return new PluginTranslator(plugin, config, registry, classLoaders);
    }

    public @NotNull Key name() {
        return new NamespacedKey(this.plugin.getName().toLowerCase(Locale.ROOT), "translator");
    }

    public @Nullable MessageFormat translate(@NotNull String key, @Nullable Locale locale) {
        if (locale == null) {
            locale = this.config.defaultLocale();
        }

        this.loadLocaleLazily(locale);
        return this.registry.translate(key, locale);
    }

    public @Nullable Component translate(@NotNull TranslatableComponent component, @Nullable Locale locale) {
        return null;
    }

    private void loadLocaleLazily(@NotNull Locale locale) {
        if (!this.loadedLocales.contains(locale)) {
            try {
                ClassLoader[] aclassloader = this.classLoaders;
                int i = aclassloader.length;
                int j = 0;

                ResourceBundle bundle;

                while (true) {
                    if (j >= i) {
                        return;
                    }

                    ClassLoader cl = aclassloader[j];

                    try {
                        bundle = ResourceBundle.getBundle(this.config.baseName(), locale, cl, new UTF8ResourceBundleControl());
                        break;
                    } catch (MissingResourceException missingresourceexception) {
                        ++j;
                    }
                }

                if (!this.loadedLocales.contains(bundle.getLocale())) {
                    this.registry.registerAll(locale, bundle, false);
                    return;
                }
            } finally {
                this.loadedLocales.add(locale);
            }

        }
    }

    public void reload() {
        this.loadedLocales.clear();
        this.registry = createRegistry(this.plugin, this.config);
    }

    private static TranslationRegistry createRegistry(@NotNull Plugin plugin, @NotNull TranslationConfig config) {
        TranslationRegistry registry = TranslationRegistry.create(Key.key(plugin.getName().toLowerCase(Locale.ROOT)));

        registry.defaultLocale(config.defaultLocale());
        return registry;
    }
}
