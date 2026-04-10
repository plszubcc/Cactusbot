package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;
import com.google.inject.ConfigurationException;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.Message;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;

public class BindingBuilder<T> extends AbstractBindingBuilder<T> implements AnnotatedBindingBuilder<T> {

    public BindingBuilder(Binder binder, List<com.google.inject.spi.Element> elements, Object source, Key<T> key) {
        super(binder, elements, source, key);
    }

    public BindingBuilder<T> annotatedWith(Class<? extends Annotation> annotationType) {
        this.annotatedWithInternal(annotationType);
        return this;
    }

    public BindingBuilder<T> annotatedWith(Annotation annotation) {
        this.annotatedWithInternal(annotation);
        return this;
    }

    public BindingBuilder<T> to(Class<? extends T> implementation) {
        return this.to(Key.get(implementation));
    }

    public BindingBuilder<T> to(TypeLiteral<? extends T> implementation) {
        return this.to(Key.get(implementation));
    }

    public BindingBuilder<T> to(Key<? extends T> linkedKey) {
        Preconditions.checkNotNull(linkedKey, "linkedKey");
        this.checkNotTargetted();
        BindingImpl<T> base = this.getBinding();

        this.setBinding(new LinkedBindingImpl(base.getSource(), base.getKey(), base.getScoping(), linkedKey));
        return this;
    }

    public void toInstance(T instance) {
        this.checkNotTargetted();
        Set<InjectionPoint> injectionPoints;

        if (instance != null) {
            try {
                injectionPoints = InjectionPoint.forInstanceMethodsAndFields(instance.getClass());
            } catch (ConfigurationException configurationexception) {
                this.copyErrorsToBinder(configurationexception);
                injectionPoints = (Set) configurationexception.getPartialValue();
            }
        } else {
            this.binder.addError("Binding to null instances is not allowed. Use toProvider(Providers.of(null)) if this is your intended behaviour.");
            injectionPoints = ImmutableSet.of();
        }

        BindingImpl<T> base = this.getBinding();

        this.setBinding(new InstanceBindingImpl(base.getSource(), base.getKey(), Scoping.EAGER_SINGLETON, injectionPoints, instance));
    }

    public BindingBuilder<T> toProvider(Provider<? extends T> provider) {
        return this.toProvider(provider);
    }

    public BindingBuilder<T> toProvider(jakarta.inject.Provider<? extends T> provider) {
        Preconditions.checkNotNull(provider, "provider");
        this.checkNotTargetted();

        Set<InjectionPoint> injectionPoints;

        try {
            injectionPoints = InjectionPoint.forInstanceMethodsAndFields(provider.getClass());
        } catch (ConfigurationException configurationexception) {
            this.copyErrorsToBinder(configurationexception);
            injectionPoints = (Set) configurationexception.getPartialValue();
        }

        BindingImpl<T> base = this.getBinding();

        this.setBinding(new ProviderInstanceBindingImpl(base.getSource(), base.getKey(), base.getScoping(), injectionPoints, provider));
        return this;
    }

    public BindingBuilder<T> toProvider(Class<? extends jakarta.inject.Provider<? extends T>> providerType) {
        return this.toProvider(Key.get(providerType));
    }

    public BindingBuilder<T> toProvider(TypeLiteral<? extends jakarta.inject.Provider<? extends T>> providerType) {
        return this.toProvider(Key.get(providerType));
    }

    public BindingBuilder<T> toProvider(Key<? extends jakarta.inject.Provider<? extends T>> providerKey) {
        Preconditions.checkNotNull(providerKey, "providerKey");
        this.checkNotTargetted();
        BindingImpl<T> base = this.getBinding();

        this.setBinding(new LinkedProviderBindingImpl(base.getSource(), base.getKey(), base.getScoping(), providerKey));
        return this;
    }

    public <S extends T> ScopedBindingBuilder toConstructor(Constructor<S> constructor) {
        return this.toConstructor(constructor, TypeLiteral.get(constructor.getDeclaringClass()));
    }

    public <S extends T> ScopedBindingBuilder toConstructor(Constructor<S> constructor, TypeLiteral<? extends S> type) {
        Preconditions.checkNotNull(constructor, "constructor");
        Preconditions.checkNotNull(type, "type");
        this.checkNotTargetted();
        BindingImpl<T> base = this.getBinding();

        Set<InjectionPoint> injectionPoints;

        try {
            injectionPoints = InjectionPoint.forInstanceMethodsAndFields(type);
        } catch (ConfigurationException configurationexception) {
            this.copyErrorsToBinder(configurationexception);
            injectionPoints = (Set) configurationexception.getPartialValue();
        }

        try {
            InjectionPoint constructorPoint = InjectionPoint.forConstructor(constructor, type);

            this.setBinding(new ConstructorBindingImpl(base.getKey(), base.getSource(), base.getScoping(), constructorPoint, injectionPoints));
        } catch (ConfigurationException configurationexception1) {
            this.copyErrorsToBinder(configurationexception1);
        }

        return this;
    }

    public String toString() {
        return "BindingBuilder<" + this.getBinding().getKey().getTypeLiteral() + ">";
    }

    private void copyErrorsToBinder(ConfigurationException e) {
        for (Message message : e.getErrorMessages()) {
            this.binder.addError(message);
        }

    }
}
