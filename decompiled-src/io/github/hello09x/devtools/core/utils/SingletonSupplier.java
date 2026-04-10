package io.github.hello09x.devtools.core.utils;

import java.util.function.Supplier;

public class SingletonSupplier<T> implements Supplier<T> {

    private volatile T instance;
    private final Supplier<T> instanceSupplier;

    public SingletonSupplier(Supplier<T> instanceSupplier) {
        super();
        this.instanceSupplier = instanceSupplier;
    }

    public T get() {
        T instance = this.instance;

        if (instance == null) {
            synchronized (this) {
                instance = this.instance;
                if (instance == null) {
                    instance = (T) this.instanceSupplier.get();
                }
            }
        }

        return instance;
    }

    public void remove() {
        this.instance = null;
    }
}
