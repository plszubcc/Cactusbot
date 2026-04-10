package io.github.hello09x.devtools.core.utils;

import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public abstract class Lambdas {

    public Lambdas() {
        super();
    }

    public static <T> T configure(@NotNull T obj, @NotNull Consumer<T> consumer) {
        consumer.accept(obj);
        return obj;
    }

    public static @NotNull Supplier<Void> asSupplier(@NotNull Runnable runnable) {
        return () -> {
            runnable.run();
            return null;
        };
    }
}
