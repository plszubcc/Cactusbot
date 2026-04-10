package com.google.inject.internal;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;
import com.google.inject.Exposed;
import com.google.inject.Key;
import com.google.inject.PrivateBinder;
import com.google.inject.Provides;
import com.google.inject.internal.util.StackTraceElements;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.HasDependencies;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.ProviderWithExtensionVisitor;
import com.google.inject.spi.ProvidesMethodBinding;
import com.google.inject.spi.ProvidesMethodTargetVisitor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.function.BiFunction;

public abstract class ProviderMethod<T> extends InternalProviderInstanceBindingImpl.CyclicFactory<T> implements HasDependencies, ProvidesMethodBinding<T>, ProviderWithExtensionVisitor<T> {

    protected final Object instance;
    protected final Method method;
    private final Key<T> key;
    private final Class<? extends Annotation> scopeAnnotation;
    private final ImmutableSet<Dependency<?>> dependencies;
    private final boolean exposed;
    private final Annotation annotation;
    private SingleParameterInjector<?>[] parameterInjectors;

    static <T> ProviderMethod<T> create(Key<T> key, Method method, Object instance, ImmutableSet<Dependency<?>> dependencies, Class<? extends Annotation> scopeAnnotation, boolean skipFastClassGeneration, Annotation annotation) {
        int modifiers = method.getModifiers();

        if (InternalFlags.isBytecodeGenEnabled() && !skipFastClassGeneration) {
            try {
                BiFunction<Object, Object[], Object> fastMethod = BytecodeGen.fastMethod(method);

                if (fastMethod != null) {
                    return new ProviderMethod.FastClassProviderMethod<T>(key, method, instance, dependencies, scopeAnnotation, annotation, fastMethod);
                }
            } catch (LinkageError | Exception exception) {
                ;
            }
        }

        if (!Modifier.isPublic(modifiers) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
            method.setAccessible(true);
        }

        return new ProviderMethod.ReflectionProviderMethod<T>(key, method, instance, dependencies, scopeAnnotation, annotation);
    }

    ProviderMethod(Key<T> key, Method method, Object instance, ImmutableSet<Dependency<?>> dependencies, Class<? extends Annotation> scopeAnnotation, Annotation annotation) {
        super(InternalProviderInstanceBindingImpl.InitializationTiming.EAGER);
        this.key = key;
        this.scopeAnnotation = scopeAnnotation;
        this.instance = instance;
        this.dependencies = dependencies;
        this.method = method;
        this.exposed = method.isAnnotationPresent(Exposed.class);
        this.annotation = annotation;
    }

    public Key<T> getKey() {
        return this.key;
    }

    public Method getMethod() {
        return this.method;
    }

    public Object getInstance() {
        return this.instance;
    }

    public Object getEnclosingInstance() {
        return this.instance;
    }

    public Annotation getAnnotation() {
        return this.annotation;
    }

    public void configure(Binder binder) {
        binder = binder.withSource(this.method);
        if (this.scopeAnnotation != null) {
            binder.bind(this.key).toProvider(this).in(this.scopeAnnotation);
        } else {
            binder.bind(this.key).toProvider(this);
        }

        if (this.exposed) {
            ((PrivateBinder) binder).expose(this.key);
        }

    }

    void initialize(InjectorImpl injector, Errors errors) throws ErrorsException {
        this.parameterInjectors = injector.getParametersInjectors(this.dependencies.asList(), errors);
    }

    protected T doProvision(InternalContext context, Dependency<?> dependency) throws InternalProvisionException {
        try {
            T t = (T) this.doProvision(SingleParameterInjector.getAll(context, this.parameterInjectors));

            if (t == null && !dependency.isNullable()) {
                InternalProvisionException.onNullInjectedIntoNonNullableDependency(this.getMethod(), dependency);
            }

            return t;
        } catch (IllegalAccessException illegalaccessexception) {
            throw new AssertionError(illegalaccessexception);
        } catch (InvocationTargetException invocationtargetexception) {
            Throwable cause = (Throwable) (invocationtargetexception.getCause() != null ? invocationtargetexception.getCause() : invocationtargetexception);

            throw InternalProvisionException.errorInProvider(cause).addSource(this.getSource());
        }
    }

    abstract T doProvision(Object[] parameters) throws IllegalAccessException, InvocationTargetException;

    public Set<Dependency<?>> getDependencies() {
        return this.dependencies;
    }

    public <B, V> V acceptExtensionVisitor(BindingTargetVisitor<B, V> visitor, ProviderInstanceBinding<? extends B> binding) {
        return (V) (visitor instanceof ProvidesMethodTargetVisitor ? ((ProvidesMethodTargetVisitor) visitor).visit(this) : visitor.visit(binding));
    }

    public String toString() {
        String annotationString = this.annotation.toString();

        if (this.annotation.annotationType() == Provides.class) {
            annotationString = "@Provides";
        } else if (annotationString.endsWith("()")) {
            annotationString = annotationString.substring(0, annotationString.length() - 2);
        }

        return annotationString + " " + StackTraceElements.forMember(this.method);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ProviderMethod)) {
            return false;
        } else {
            ProviderMethod<?> o = (ProviderMethod) obj;

            return this.method.equals(o.method) && Objects.equal(this.instance, o.instance) && this.annotation.equals(o.annotation);
        }
    }

    public int hashCode() {
        return Objects.hashCode(new Object[]{this.method, this.annotation});
    }

    private static final class FastClassProviderMethod<T> extends ProviderMethod<T> {

        final BiFunction<Object, Object[], Object> fastMethod;

        FastClassProviderMethod(Key<T> key, Method method, Object instance, ImmutableSet<Dependency<?>> dependencies, Class<? extends Annotation> scopeAnnotation, Annotation annotation, BiFunction<Object, Object[], Object> fastMethod) {
            super(key, method, instance, dependencies, scopeAnnotation, annotation);
            this.fastMethod = fastMethod;
        }

        public T doProvision(Object[] parameters) throws InvocationTargetException {
            try {
                return (T) this.fastMethod.apply(this.instance, parameters);
            } catch (Throwable throwable) {
                throw new InvocationTargetException(throwable);
            }
        }
    }

    private static final class ReflectionProviderMethod<T> extends ProviderMethod<T> {

        ReflectionProviderMethod(Key<T> key, Method method, Object instance, ImmutableSet<Dependency<?>> dependencies, Class<? extends Annotation> scopeAnnotation, Annotation annotation) {
            super(key, method, instance, dependencies, scopeAnnotation, annotation);
        }

        T doProvision(Object[] parameters) throws IllegalAccessException, InvocationTargetException {
            return (T) this.method.invoke(this.instance, parameters);
        }
    }
}
