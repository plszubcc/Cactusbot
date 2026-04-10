package com.google.inject.internal;

import com.google.inject.spi.Dependency;
import java.util.IdentityHashMap;

final class InternalContext implements AutoCloseable {

    private final InjectorImpl.InjectorOptions options;
    private final IdentityHashMap<Object, ConstructionContext<?>> constructionContexts = new IdentityHashMap();
    private Dependency<?> dependency;
    private int enterCount;
    private final Object[] toClear;

    InternalContext(InjectorImpl.InjectorOptions options, Object[] toClear) {
        super();
        this.options = options;
        this.toClear = toClear;
        this.enterCount = 1;
    }

    void enter() {
        ++this.enterCount;
    }

    public void close() {
        int newCount = --this.enterCount;

        if (newCount < 0) {
            throw new IllegalStateException("Called close() too many times");
        } else {
            if (newCount == 0) {
                this.toClear[0] = null;
            }

        }
    }

    InjectorImpl.InjectorOptions getInjectorOptions() {
        return this.options;
    }

    <T> ConstructionContext<T> getConstructionContext(Object key) {
        ConstructionContext<T> constructionContext = (ConstructionContext) this.constructionContexts.get(key);

        if (constructionContext == null) {
            constructionContext = new ConstructionContext<T>();
            this.constructionContexts.put(key, constructionContext);
        }

        return constructionContext;
    }

    Dependency<?> getDependency() {
        return this.dependency;
    }

    void setDependency(Dependency<?> dependency) {
        this.dependency = dependency;
    }
}
