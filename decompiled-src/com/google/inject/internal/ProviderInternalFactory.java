package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.inject.spi.Dependency;
import jakarta.inject.Provider;
import javax.annotation.Nullable;

abstract class ProviderInternalFactory<T> implements InternalFactory<T> {

    protected final Object source;

    ProviderInternalFactory(Object source) {
        super();
        this.source = Preconditions.checkNotNull(source, "source");
    }

    protected T circularGet(final Provider<? extends T> provider, InternalContext context, final Dependency<?> dependency, @Nullable ProvisionListenerStackCallback<T> provisionCallback) throws InternalProvisionException {
        final ConstructionContext<T> constructionContext = context.<T>getConstructionContext(this);

        if (constructionContext.isConstructing()) {
            Class<?> expectedType = dependency.getKey().getTypeLiteral().getRawType();
            T proxyType = (T) constructionContext.createProxy(context.getInjectorOptions(), expectedType);

            return proxyType;
        } else {
            constructionContext.startConstruction();

            Object expectedType;

            try {
                if (provisionCallback != null) {
                    expectedType = provisionCallback.provision(context, new ProvisionListenerStackCallback.ProvisionCallback<T>() {
                        public T call() throws InternalProvisionException {
                            return (T) ProviderInternalFactory.this.provision(provider, dependency, constructionContext);
                        }
                    });
                    return (T) expectedType;
                }

                expectedType = this.provision(provider, dependency, constructionContext);
            } finally {
                constructionContext.removeCurrentReference();
                constructionContext.finishConstruction();
            }

            return (T) expectedType;
        }
    }

    protected T provision(Provider<? extends T> provider, Dependency<?> dependency, ConstructionContext<T> constructionContext) throws InternalProvisionException {
        T t = provider.get();

        if (t == null && !dependency.isNullable()) {
            InternalProvisionException.onNullInjectedIntoNonNullableDependency(this.source, dependency);
        }

        constructionContext.setProxyDelegates(t);
        return t;
    }
}
