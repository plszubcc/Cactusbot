package com.google.inject.internal;

import com.google.common.base.MoreObjects;
import com.google.inject.Key;
import com.google.inject.spi.Dependency;

final class FactoryProxy<T> implements InternalFactory<T>, CreationListener {

    private final InjectorImpl injector;
    private final Key<T> key;
    private final Key<? extends T> targetKey;
    private final Object source;
    private InternalFactory<? extends T> targetFactory;

    FactoryProxy(InjectorImpl injector, Key<T> key, Key<? extends T> targetKey, Object source) {
        super();
        this.injector = injector;
        this.key = key;
        this.targetKey = targetKey;
        this.source = source;
    }

    public void notify(Errors errors) {
        try {
            this.targetFactory = this.injector.<T>getInternalFactory(this.targetKey, errors.withSource(this.source), InjectorImpl.JitLimitation.NEW_OR_EXISTING_JIT);
        } catch (ErrorsException errorsexception) {
            errors.merge(errorsexception.getErrors());
        }

    }

    public T get(InternalContext context, Dependency<?> dependency, boolean linked) throws InternalProvisionException {
        Key<? extends T> localTargetKey = this.targetKey;

        try {
            return this.targetFactory.get(context, dependency, true);
        } catch (InternalProvisionException internalprovisionexception) {
            throw internalprovisionexception.addSource(localTargetKey);
        }
    }

    public String toString() {
        return MoreObjects.toStringHelper(FactoryProxy.class).add("key", this.key).add("provider", this.targetFactory).toString();
    }
}
