package com.google.inject.internal;

import com.google.inject.Key;
import com.google.inject.spi.Dependency;
import jakarta.inject.Provider;

class ProvidedByInternalFactory<T> extends ProviderInternalFactory<T> implements DelayedInitialize {

    private final Class<?> rawType;
    private final Class<? extends Provider<?>> providerType;
    private final Key<? extends Provider<T>> providerKey;
    private BindingImpl<? extends Provider<T>> providerBinding;
    private ProvisionListenerStackCallback<T> provisionCallback;

    ProvidedByInternalFactory(Class<?> rawType, Class<? extends Provider<?>> providerType, Key<? extends Provider<T>> providerKey) {
        super(providerKey);
        this.rawType = rawType;
        this.providerType = providerType;
        this.providerKey = providerKey;
    }

    void setProvisionListenerCallback(ProvisionListenerStackCallback<T> listener) {
        this.provisionCallback = listener;
    }

    public void initialize(InjectorImpl injector, Errors errors) throws ErrorsException {
        this.providerBinding = injector.<Provider<T>>getBindingOrThrow(this.providerKey, errors, InjectorImpl.JitLimitation.NEW_OR_EXISTING_JIT);
    }

    public T get(InternalContext context, Dependency<?> dependency, boolean linked) throws InternalProvisionException {
        BindingImpl<? extends Provider<T>> localProviderBinding = this.providerBinding;

        if (localProviderBinding == null) {
            throw new IllegalStateException("not initialized");
        } else {
            Key<? extends Provider<T>> localProviderKey = this.providerKey;

            try {
                Provider<? extends T> provider = localProviderBinding.getInternalFactory().get(context, dependency, true);

                return (T) this.circularGet(provider, context, dependency, this.provisionCallback);
            } catch (InternalProvisionException internalprovisionexception) {
                throw internalprovisionexception.addSource(localProviderKey);
            }
        }
    }

    protected T provision(Provider<? extends T> provider, Dependency<?> dependency, ConstructionContext<T> constructionContext) throws InternalProvisionException {
        try {
            Object o = super.provision(provider, dependency, constructionContext);

            if (o != null && !this.rawType.isInstance(o)) {
                throw InternalProvisionException.subtypeNotProvided(this.providerType, this.rawType);
            } else {
                return (T) o;
            }
        } catch (RuntimeException runtimeexception) {
            throw InternalProvisionException.errorInProvider(runtimeexception).addSource(this.source);
        }
    }
}
