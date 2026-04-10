package com.google.inject.internal;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.HasDependencies;

final class InternalProviderInstanceBindingImpl<T> extends ProviderInstanceBindingImpl<T> implements DelayedInitialize {

    private final InternalProviderInstanceBindingImpl.Factory<T> originalFactory;

    InternalProviderInstanceBindingImpl(InjectorImpl injector, Key<T> key, Object source, InternalProviderInstanceBindingImpl.Factory<T> originalFactory, InternalFactory<? extends T> scopedFactory, Scoping scoping) {
        super(injector, key, source, scopedFactory, scoping, originalFactory, ImmutableSet.of());
        this.originalFactory = originalFactory;
    }

    InternalProviderInstanceBindingImpl.InitializationTiming getInitializationTiming() {
        return this.originalFactory.initializationTiming;
    }

    public void initialize(InjectorImpl injector, Errors errors) throws ErrorsException {
        this.originalFactory.source = this.getSource();
        this.originalFactory.provisionCallback = injector.provisionListenerStore.<T>get(this);
        this.originalFactory.delegateProvider = this.getProvider();
        this.originalFactory.initialize(injector, errors);
    }

    static enum InitializationTiming {

        EAGER, DELAYED;

        private InitializationTiming() {}
    }

    abstract static class Factory<T> implements InternalFactory<T>, Provider<T>, HasDependencies {

        private final InternalProviderInstanceBindingImpl.InitializationTiming initializationTiming;
        private Object source;
        private Provider<T> delegateProvider;
        ProvisionListenerStackCallback<T> provisionCallback;

        Factory(InternalProviderInstanceBindingImpl.InitializationTiming initializationTiming) {
            super();
            this.initializationTiming = initializationTiming;
        }

        final Object getSource() {
            return this.source;
        }

        abstract void initialize(InjectorImpl injector, Errors errors) throws ErrorsException;

        public final T get() {
            Provider<T> local = this.delegateProvider;

            if (local == null) {
                throw new IllegalStateException("This Provider cannot be used until the Injector has been created.");
            } else {
                return (T) local.get();
            }
        }

        public T get(final InternalContext context, final Dependency<?> dependency, boolean linked) throws InternalProvisionException {
            return (T) (this.provisionCallback == null ? this.doProvision(context, dependency) : this.provisionCallback.provision(context, new ProvisionListenerStackCallback.ProvisionCallback<T>() {
                public T call() throws InternalProvisionException {
                    return (T) Factory.this.doProvision(context, dependency);
                }
            }));
        }

        protected abstract T doProvision(InternalContext context, Dependency<?> dependency) throws InternalProvisionException;
    }

    abstract static class CyclicFactory<T> extends InternalProviderInstanceBindingImpl.Factory<T> {

        CyclicFactory(InternalProviderInstanceBindingImpl.InitializationTiming initializationTiming) {
            super(initializationTiming);
        }

        public final T get(final InternalContext context, final Dependency<?> dependency, boolean linked) throws InternalProvisionException {
            final ConstructionContext<T> constructionContext = context.<T>getConstructionContext(this);

            if (constructionContext.isConstructing()) {
                Class<?> expectedType = dependency.getKey().getTypeLiteral().getRawType();
                T proxyType = (T) constructionContext.createProxy(context.getInjectorOptions(), expectedType);

                return proxyType;
            } else {
                constructionContext.startConstruction();

                Object expectedType;

                try {
                    if (this.provisionCallback != null) {
                        expectedType = this.provisionCallback.provision(context, new ProvisionListenerStackCallback.ProvisionCallback<T>() {
                            public T call() throws InternalProvisionException {
                                return (T) CyclicFactory.this.provision(dependency, context, constructionContext);
                            }
                        });
                        return (T) expectedType;
                    }

                    expectedType = this.provision(dependency, context, constructionContext);
                } finally {
                    constructionContext.removeCurrentReference();
                    constructionContext.finishConstruction();
                }

                return (T) expectedType;
            }
        }

        private T provision(Dependency<?> dependency, InternalContext context, ConstructionContext<T> constructionContext) throws InternalProvisionException {
            try {
                T t = (T) this.doProvision(context, dependency);

                constructionContext.setProxyDelegates(t);
                return t;
            } catch (InternalProvisionException internalprovisionexception) {
                throw internalprovisionexception.addSource(this.getSource());
            } catch (Throwable throwable) {
                throw InternalProvisionException.errorInProvider(throwable).addSource(this.getSource());
            }
        }
    }
}
