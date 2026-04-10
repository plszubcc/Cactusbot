package com.google.inject.util;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CheckReturnValue;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.ProviderWithDependencies;
import java.util.Set;

@CheckReturnValue
public final class Providers {

    private Providers() {
        super();
    }

    public static <T> Provider<T> of(T instance) {
        return new Providers.ConstantProvider<T>(instance);
    }

    /** @deprecated */
    @Deprecated
    public static <T> Provider<T> guicify(Provider<T> provider) {
        return provider;
    }

    public static <T> Provider<T> guicify(jakarta.inject.Provider<T> provider) {
        if (provider instanceof Provider) {
            return (Provider) provider;
        } else {
            jakarta.inject.Provider<T> delegate = (jakarta.inject.Provider) Preconditions.checkNotNull(provider, "provider");
            Set<InjectionPoint> injectionPoints = InjectionPoint.forInstanceMethodsAndFields(provider.getClass());

            if (injectionPoints.isEmpty()) {
                return new Providers.GuicifiedJakartaProvider<T>(delegate);
            } else {
                ImmutableSet<Dependency<?>> dependencies = (ImmutableSet) injectionPoints.stream().flatMap((ipx) -> {
                    return ipx.getDependencies().stream();
                }).collect(ImmutableSet.toImmutableSet());

                return new Providers.GuicifiedJakartaProviderWithDependencies<T>(dependencies, delegate);
            }
        }
    }

    private static final class ConstantProvider<T> implements Provider<T> {

        private final T instance;

        private ConstantProvider(T instance) {
            super();
            this.instance = instance;
        }

        public T get() {
            return this.instance;
        }

        public String toString() {
            return "of(" + this.instance + ")";
        }

        public boolean equals(Object obj) {
            return obj instanceof Providers.ConstantProvider && Objects.equal(this.instance, ((Providers.ConstantProvider) obj).instance);
        }

        public int hashCode() {
            return Objects.hashCode(new Object[]{this.instance});
        }
    }

    private static class GuicifiedJakartaProvider<T> implements Provider<T> {

        protected final jakarta.inject.Provider<T> delegate;

        private GuicifiedJakartaProvider(jakarta.inject.Provider<T> delegate) {
            super();
            this.delegate = delegate;
        }

        public T get() {
            return this.delegate.get();
        }

        public String toString() {
            return "guicified(" + this.delegate + ")";
        }

        public boolean equals(Object obj) {
            return obj instanceof Providers.GuicifiedJakartaProvider && Objects.equal(this.delegate, ((Providers.GuicifiedJakartaProvider) obj).delegate);
        }

        public int hashCode() {
            return Objects.hashCode(new Object[]{this.delegate});
        }
    }

    private static final class GuicifiedJakartaProviderWithDependencies<T> extends Providers.GuicifiedJakartaProvider<T> implements ProviderWithDependencies<T> {

        private final Set<Dependency<?>> dependencies;

        private GuicifiedJakartaProviderWithDependencies(Set<Dependency<?>> dependencies, jakarta.inject.Provider<T> delegate) {
            super(delegate, null);
            this.dependencies = dependencies;
        }

        @Inject
        void initialize(Injector injector) {
            injector.injectMembers(this.delegate);
        }

        public Set<Dependency<?>> getDependencies() {
            return this.dependencies;
        }
    }
}
