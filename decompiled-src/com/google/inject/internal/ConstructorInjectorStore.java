package com.google.inject.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.InterceptorBinding;

final class ConstructorInjectorStore {

    private final InjectorImpl injector;
    private final FailableCache<InjectionPoint, ConstructorInjector<?>> cache = new FailableCache<InjectionPoint, ConstructorInjector<?>>() {
        protected ConstructorInjector<?> create(InjectionPoint constructorInjector, Errors errors) throws ErrorsException {
            return ConstructorInjectorStore.this.createConstructor(constructorInjector, errors);
        }
    };

    ConstructorInjectorStore(InjectorImpl injector) {
        super();
        this.injector = injector;
    }

    boolean isLoading(InjectionPoint ip) {
        return this.cache.isLoading(ip);
    }

    public ConstructorInjector<?> get(InjectionPoint constructorInjector, Errors errors) throws ErrorsException {
        return this.cache.get(constructorInjector, errors);
    }

    boolean remove(InjectionPoint ip) {
        return this.cache.remove(ip);
    }

    private <T> ConstructorInjector<T> createConstructor(InjectionPoint injectionPoint, Errors errors) throws ErrorsException {
        int numErrorsBefore = errors.size();
        SingleParameterInjector<?>[] constructorParameterInjectors = this.injector.getParametersInjectors(injectionPoint.getDependencies(), errors);
        MembersInjectorImpl<T> membersInjector = this.injector.membersInjectorStore.<T>get(injectionPoint.getDeclaringType(), errors);
        ConstructionProxyFactory<T> factory = null;

        if (InternalFlags.isBytecodeGenEnabled()) {
            ImmutableList<InterceptorBinding> injectorBindings = this.injector.getBindingData().getInterceptorBindings();
            ImmutableList<MethodAspect> methodAspects = ImmutableList.builder().addAll(Lists.transform(injectorBindings, MethodAspect::fromBinding)).addAll(membersInjector.getAddedAspects()).build();

            factory = new ProxyFactory<T>(injectionPoint, methodAspects);
        } else {
            factory = new DefaultConstructionProxyFactory<T>(injectionPoint);
        }

        errors.throwIfNewErrors(numErrorsBefore);
        return new ConstructorInjector<T>(membersInjector.getInjectionPoints(), factory.create(), constructorParameterInjectors, membersInjector);
    }
}
