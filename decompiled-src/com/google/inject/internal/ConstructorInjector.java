package com.google.inject.internal;

import com.google.common.collect.ImmutableSet;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.InjectionPoint;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import javax.annotation.Nullable;

final class ConstructorInjector<T> {

    private final ImmutableSet<InjectionPoint> injectableMembers;
    private final SingleParameterInjector<?>[] parameterInjectors;
    private final ConstructionProxy<T> constructionProxy;
    private final MembersInjectorImpl<T> membersInjector;

    ConstructorInjector(Set<InjectionPoint> injectableMembers, ConstructionProxy<T> constructionProxy, SingleParameterInjector<?>[] parameterInjectors, MembersInjectorImpl<T> membersInjector) {
        super();
        this.injectableMembers = ImmutableSet.copyOf(injectableMembers);
        this.constructionProxy = constructionProxy;
        this.parameterInjectors = parameterInjectors;
        this.membersInjector = membersInjector;
    }

    public ImmutableSet<InjectionPoint> getInjectableMembers() {
        return this.injectableMembers;
    }

    ConstructionProxy<T> getConstructionProxy() {
        return this.constructionProxy;
    }

    Object construct(final InternalContext context, Dependency<?> dependency, @Nullable ProvisionListenerStackCallback<T> provisionCallback) throws InternalProvisionException {
        final ConstructionContext<T> constructionContext = context.<T>getConstructionContext(this);

        if (constructionContext.isConstructing()) {
            return constructionContext.createProxy(context.getInjectorOptions(), dependency.getKey().getTypeLiteral().getRawType());
        } else {
            T t = constructionContext.getCurrentReference();

            if (t != null) {
                if (context.getInjectorOptions().disableCircularProxies) {
                    throw InternalProvisionException.circularDependenciesDisabled(dependency.getKey().getTypeLiteral().getRawType());
                } else {
                    return t;
                }
            } else {
                constructionContext.startConstruction();

                Object object;

                try {
                    if (provisionCallback != null) {
                        object = provisionCallback.provision(context, new ProvisionListenerStackCallback.ProvisionCallback<T>() {
                            public T call() throws InternalProvisionException {
                                return (T) ConstructorInjector.this.provision(context, constructionContext);
                            }
                        });
                        return object;
                    }

                    object = this.provision(context, constructionContext);
                } finally {
                    constructionContext.finishConstruction();
                }

                return object;
            }
        }
    }

    private T provision(InternalContext context, ConstructionContext<T> constructionContext) throws InternalProvisionException {
        Object object;

        try {
            T t;

            try {
                Object[] parameters = SingleParameterInjector.getAll(context, this.parameterInjectors);

                t = this.constructionProxy.newInstance(parameters);
                constructionContext.setProxyDelegates(t);
            } finally {
                constructionContext.finishConstruction();
            }

            constructionContext.setCurrentReference(t);
            MembersInjectorImpl<T> localMembersInjector = this.membersInjector;

            localMembersInjector.injectMembers(t, context, false);
            localMembersInjector.notifyListeners(t);
            object = t;
        } catch (InvocationTargetException invocationtargetexception) {
            Throwable cause = (Throwable) (invocationtargetexception.getCause() != null ? invocationtargetexception.getCause() : invocationtargetexception);

            throw InternalProvisionException.errorInjectingConstructor(cause).addSource(this.constructionProxy.getInjectionPoint());
        } finally {
            constructionContext.removeCurrentReference();
        }

        return (T) object;
    }
}
