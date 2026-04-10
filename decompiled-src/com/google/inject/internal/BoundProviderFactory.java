package com.google.inject.internal;

import com.google.inject.Key;
import com.google.inject.spi.Dependency;
import jakarta.inject.Provider;

final class BoundProviderFactory<T> extends ProviderInternalFactory<T> implements CreationListener {

    private final ProvisionListenerStackCallback<T> provisionCallback;
    private final InjectorImpl injector;
    final Key<? extends Provider<? extends T>> providerKey;
    private InternalFactory<? extends Provider<? extends T>> providerFactory;

    BoundProviderFactory(InjectorImpl injector, Key<? extends Provider<? extends T>> providerKey, Object source, ProvisionListenerStackCallback<T> provisionCallback) {
        super(source);
        this.provisionCallback = provisionCallback;
        this.injector = injector;
        this.providerKey = providerKey;
    }

    public void notify(Errors errors) {
        try {
            this.providerFactory = this.injector.<Provider<T>>getInternalFactory(this.providerKey, errors.withSource(this.source), InjectorImpl.JitLimitation.NEW_OR_EXISTING_JIT);
        } catch (ErrorsException errorsexception) {
            errors.merge(errorsexception.getErrors());
        }

    }

    public T get(InternalContext context, Dependency<?> dependency, boolean linked) throws InternalProvisionException {
        try {
            Provider<? extends T> provider = this.providerFactory.get(context, dependency, true);

            return (T) this.circularGet(provider, context, dependency, this.provisionCallback);
        } catch (InternalProvisionException internalprovisionexception) {
            throw internalprovisionexception.addSource(this.providerKey);
        }
    }

    protected T provision(Provider<? extends T> provider, Dependency<?> dependency, ConstructionContext<T> constructionContext) throws InternalProvisionException {
        try {
            return (T) super.provision(provider, dependency, constructionContext);
        } catch (RuntimeException runtimeexception) {
            throw InternalProvisionException.errorInProvider(runtimeexception);
        }
    }

    public String toString() {
        return this.providerKey.toString();
    }
}
