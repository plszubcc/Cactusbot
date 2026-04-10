package com.google.inject.internal;

import com.google.inject.spi.Dependency;

final class SingleParameterInjector<T> {

    private static final Object[] NO_ARGUMENTS = new Object[0];
    private final Dependency<T> dependency;
    private final Object source;
    private final InternalFactory<? extends T> factory;

    SingleParameterInjector(Dependency<T> dependency, BindingImpl<? extends T> binding) {
        super();
        this.dependency = dependency;
        this.source = binding.getSource();
        this.factory = binding.getInternalFactory();
    }

    T inject(InternalContext context) throws InternalProvisionException {
        Dependency<T> localDependency = this.dependency;

        try {
            return this.factory.get(context, localDependency, false);
        } catch (InternalProvisionException internalprovisionexception) {
            throw internalprovisionexception.addSource(localDependency);
        }
    }

    static Object[] getAll(InternalContext context, SingleParameterInjector<?>[] parameterInjectors) throws InternalProvisionException {
        if (parameterInjectors == null) {
            return SingleParameterInjector.NO_ARGUMENTS;
        } else {
            int size = parameterInjectors.length;
            Object[] parameters = new Object[size];

            for (int i = 0; i < size; ++i) {
                parameters[i] = parameterInjectors[i].inject(context);
            }

            return parameters;
        }
    }
}
