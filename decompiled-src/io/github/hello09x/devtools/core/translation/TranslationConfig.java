package io.github.hello09x.devtools.core.translation;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public record TranslationConfig(@NotNull String baseName, @NotNull Locale defaultLocale) {

    public TranslationConfig {
        super();
    }
}
