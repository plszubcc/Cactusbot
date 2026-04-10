package io.github.hello09x.devtools.core;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.github.hello09x.devtools.core.translation.PluginTranslator;
import io.github.hello09x.devtools.core.translation.TranslationConfig;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class TranslationModule extends AbstractModule {

    private final TranslationConfig config;

    public TranslationModule(TranslationConfig config) {
        super();
        this.config = config;
    }

    @Provides
    @Singleton
    public PluginTranslator pluginTranslator(@NotNull Plugin plugin) {
        PluginTranslator translator = PluginTranslator.of(plugin, this.config);

        GlobalTranslator.translator().addSource(translator);
        return translator;
    }
}
