package io.github.hello09x.devtools.core.utils.lang;

import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public class LazyInitializer<T> implements Supplier<T> {

    private static final Object NIL = new Object();
    private volatile T instance;
    private Supplier<T> supplier;

    protected LazyInitializer(@NotNull Supplier<T> supplier) {
        super();
        this.supplier = supplier;
    }

    public static <T> LazyInitializer<T> of(@NotNull Supplier<T> supplier) {
        return new LazyInitializer<T>(supplier);
    }

    public T get() {
        if (this.instance == null) {
            synchronized (this) {
                if (this.instance == null) {
                    T instance = (T) this.supplier.get();

                    if (instance == null) {
                        instance = (T) LazyInitializer.NIL;
                    }

                    this.instance = instance;
                    this.supplier = null;
                }
            }
        }

        return (T) (this.instance == LazyInitializer.NIL ? null : this.instance);
    }
}
