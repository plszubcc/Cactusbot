package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.inject.spi.Dependency;
import jakarta.inject.Provider;

final class InternalFactoryToInitializableAdapter<T> extends ProviderInternalFactory<T> {

    private final ProvisionListenerStackCallback<T> provisionCallback;
    private final Initializable<? extends Provider<? extends T>> initializable;

    public InternalFactoryToInitializableAdapter(Initializable<? extends Provider<? extends T>> initializable, Object source, ProvisionListenerStackCallback<T> provisionCallback) {
        super(source);
        this.provisionCallback = provisionCallback;
        this.initializable = (Initializable) Preconditions.checkNotNull(initializable, "provider");
    }

    public T get(InternalContext context, Dependency<?> dependency, boolean linked) throws InternalProvisionException {
        return (T) this.circularGet(this.initializable.get(), context, dependency, this.provisionCallback);
    }

    protected T provision(Provider<? extends T> provider, Dependency<?> dependency, ConstructionContext<T> constructionContext) throws InternalProvisionException {
        try {
            return (T) super.provision(provider, dependency, constructionContext);
        } catch (RuntimeException runtimeexception) {
            throw InternalProvisionException.errorInProvider(runtimeexception).addSource(this.source);
        }
    }

    public String toString() {
        return this.initializable.toString();
    }
}
