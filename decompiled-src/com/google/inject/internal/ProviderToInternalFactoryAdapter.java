package com.google.inject.internal;

import com.google.inject.Provider;

final class ProviderToInternalFactoryAdapter<T> implements Provider<T> {

    private final InjectorImpl injector;
    private final InternalFactory<? extends T> internalFactory;

    public ProviderToInternalFactoryAdapter(InjectorImpl injector, InternalFactory<? extends T> internalFactory) {
        super();
        this.injector = injector;
        this.internalFactory = internalFactory;
    }

    public T get() {
        InternalContext context = this.injector.enterContext();

        Object object;

        try {
            T t = this.internalFactory.get(context, context.getDependency(), true);

            object = t;
        } catch (InternalProvisionException internalprovisionexception) {
            throw internalprovisionexception.toProvisionException();
        } finally {
            context.close();
        }

        return (T) object;
    }

    InjectorImpl getInjector() {
        return this.injector;
    }

    public String toString() {
        return this.internalFactory.toString();
    }
}
