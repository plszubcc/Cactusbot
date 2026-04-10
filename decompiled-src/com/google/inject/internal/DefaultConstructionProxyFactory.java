package com.google.inject.internal;

import com.google.common.collect.ImmutableMap;
import com.google.inject.spi.InjectionPoint;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.function.BiFunction;
import org.aopalliance.intercept.MethodInterceptor;

final class DefaultConstructionProxyFactory<T> implements ConstructionProxyFactory<T> {

    private final InjectionPoint injectionPoint;

    DefaultConstructionProxyFactory(InjectionPoint injectionPoint) {
        super();
        this.injectionPoint = injectionPoint;
    }

    public ConstructionProxy<T> create() {
        Constructor<T> constructor = (Constructor) this.injectionPoint.getMember();

        if (InternalFlags.isBytecodeGenEnabled()) {
            try {
                BiFunction<Object, Object[], Object> fastConstructor = BytecodeGen.fastConstructor(constructor);

                if (fastConstructor != null) {
                    return new DefaultConstructionProxyFactory.FastClassProxy<T>(this.injectionPoint, constructor, fastConstructor);
                }
            } catch (LinkageError | Exception exception) {
                ;
            }
        }

        return new DefaultConstructionProxyFactory.ReflectiveProxy<T>(this.injectionPoint, constructor);
    }

    private static final class FastClassProxy<T> implements ConstructionProxy<T> {

        final InjectionPoint injectionPoint;
        final Constructor<T> constructor;
        final BiFunction<Object, Object[], Object> fastConstructor;

        FastClassProxy(InjectionPoint injectionPoint, Constructor<T> constructor, BiFunction<Object, Object[], Object> fastConstructor) {
            super();
            this.injectionPoint = injectionPoint;
            this.constructor = constructor;
            this.fastConstructor = fastConstructor;
        }

        public T newInstance(Object... arguments) throws InvocationTargetException {
            try {
                return (T) this.fastConstructor.apply((Object) null, arguments);
            } catch (Throwable throwable) {
                throw new InvocationTargetException(throwable);
            }
        }

        public InjectionPoint getInjectionPoint() {
            return this.injectionPoint;
        }

        public Constructor<T> getConstructor() {
            return this.constructor;
        }

        public ImmutableMap<Method, List<MethodInterceptor>> getMethodInterceptors() {
            return ImmutableMap.of();
        }
    }

    private static final class ReflectiveProxy<T> implements ConstructionProxy<T> {

        final Constructor<T> constructor;
        final InjectionPoint injectionPoint;

        ReflectiveProxy(InjectionPoint injectionPoint, Constructor<T> constructor) {
            super();
            if (!Modifier.isPublic(constructor.getDeclaringClass().getModifiers()) || !Modifier.isPublic(constructor.getModifiers())) {
                constructor.setAccessible(true);
            }

            this.injectionPoint = injectionPoint;
            this.constructor = constructor;
        }

        public T newInstance(Object... arguments) throws InvocationTargetException {
            try {
                return (T) this.constructor.newInstance(arguments);
            } catch (InstantiationException instantiationexception) {
                throw new AssertionError(instantiationexception);
            } catch (IllegalAccessException illegalaccessexception) {
                throw new AssertionError(illegalaccessexception);
            }
        }

        public InjectionPoint getInjectionPoint() {
            return this.injectionPoint;
        }

        public Constructor<T> getConstructor() {
            return this.constructor;
        }

        public ImmutableMap<Method, List<MethodInterceptor>> getMethodInterceptors() {
            return ImmutableMap.of();
        }
    }
}
