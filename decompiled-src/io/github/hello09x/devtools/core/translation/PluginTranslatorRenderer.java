package io.github.hello09x.devtools.core.translation;

import java.text.MessageFormat;
import java.util.Locale;
import net.kyori.adventure.text.renderer.TranslatableComponentRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PluginTranslatorRenderer extends TranslatableComponentRenderer<Locale> {

    private final PluginTranslator translator;

    public PluginTranslatorRenderer(PluginTranslator translator) {
        super();
        this.translator = translator;
    }

    protected @Nullable MessageFormat translate(@NotNull String key, @NotNull Locale context) {
        return this.translator.translate(key, context);
    }
}
