package com.google.inject.internal;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.inject.spi.InjectionPoint;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.aopalliance.intercept.MethodInterceptor;

final class ProxyFactory<T> implements ConstructionProxyFactory<T> {

    private static final Logger logger = Logger.getLogger(ProxyFactory.class.getName());
    private final InjectionPoint injectionPoint;
    private final Function<String, BiFunction<Object, Object[], Object>> enhancer;
    private final ImmutableMap<Method, List<MethodInterceptor>> interceptors;
    private final InvocationHandler[] callbacks;

    ProxyFactory(InjectionPoint injectionPoint, Iterable<MethodAspect> methodAspects) throws ErrorsException {
        super();
        this.injectionPoint = injectionPoint;
        Class<?> hostClass = injectionPoint.getMember().getDeclaringClass();
        List<MethodAspect> applicableAspects = Lists.newArrayList();

        for (MethodAspect methodAspect : methodAspects) {
            if (methodAspect.matches(hostClass)) {
                applicableAspects.add(methodAspect);
            }
        }

        if (applicableAspects.isEmpty()) {
            this.enhancer = null;
            this.interceptors = ImmutableMap.of();
            this.callbacks = null;
        } else {
            BytecodeGen.EnhancerBuilder enhancerBuilder = BytecodeGen.enhancerBuilder(hostClass);
            Method[] methods = enhancerBuilder.getEnhanceableMethods();
            int numMethods = methods.length;
            Multimap<Method, MethodInterceptor> matchedInterceptors = ArrayListMultimap.create();
            BitSet matchedMethodIndices = new BitSet();

            for (MethodAspect methodAspect : applicableAspects) {
                for (int methodIndex = 0; methodIndex < numMethods; ++methodIndex) {
                    Method method = methods[methodIndex];

                    if (methodAspect.matches(method)) {
                        if (method.isSynthetic()) {
                            ProxyFactory.logger.log(Level.WARNING, "Method [{0}] is synthetic and is being intercepted by {1}. This could indicate a bug.  The method may be intercepted twice, or may not be intercepted at all.", new Object[]{method, methodAspect.interceptors()});
                        }

                        matchedInterceptors.putAll(method, methodAspect.interceptors());
                        matchedMethodIndices.set(methodIndex);
                    }
                }
            }

            if (matchedMethodIndices.isEmpty()) {
                this.enhancer = null;
                this.interceptors = ImmutableMap.of();
                this.callbacks = null;
            } else {
                try {
                    this.enhancer = enhancerBuilder.buildEnhancer(matchedMethodIndices);
                } catch (Throwable throwable) {
                    throw (new Errors()).errorEnhancingClass(hostClass, throwable).toException();
                }

                this.callbacks = new InvocationHandler[matchedMethodIndices.cardinality()];
                ImmutableMap.Builder<Method, List<MethodInterceptor>> interceptorsMapBuilder = ImmutableMap.builder();
                int callbackIndex = 0;

                for (int methodIndex = matchedMethodIndices.nextSetBit(0); methodIndex >= 0; methodIndex = matchedMethodIndices.nextSetBit(methodIndex + 1)) {
                    Method method = methods[methodIndex];
                    List<MethodInterceptor> deDuplicated = ImmutableSet.copyOf(matchedInterceptors.get(method)).asList();

                    interceptorsMapBuilder.put(method, deDuplicated);
                    BiFunction<Object, Object[], Object> superInvoker = BytecodeGen.superMethod(this.enhancer, method);

                    this.callbacks[callbackIndex++] = new InterceptorStackCallback(method, deDuplicated, superInvoker);
                }

                this.interceptors = interceptorsMapBuilder.buildOrThrow();
            }
        }
    }

    public ImmutableMap<Method, List<MethodInterceptor>> getInterceptors() {
        return this.interceptors;
    }

    public ConstructionProxy<T> create() throws ErrorsException {
        if (this.interceptors.isEmpty()) {
            return (new DefaultConstructionProxyFactory(this.injectionPoint)).create();
        } else {
            try {
                return new ProxyFactory.ProxyConstructor<T>(this.injectionPoint, this.enhancer, this.interceptors, this.callbacks);
            } catch (Throwable throwable) {
                throw (new Errors()).errorEnhancingClass(this.injectionPoint.getMember().getDeclaringClass(), throwable).toException();
            }
        }
    }

    private static class ProxyConstructor<T> implements ConstructionProxy<T> {

        final InjectionPoint injectionPoint;
        final Constructor<T> constructor;
        final BiFunction<Object, Object[], Object> enhancedConstructor;
        final ImmutableMap<Method, List<MethodInterceptor>> interceptors;
        final InvocationHandler[] callbacks;

        ProxyConstructor(InjectionPoint injectionPoint, Function<String, BiFunction<Object, Object[], Object>> enhancer, ImmutableMap<Method, List<MethodInterceptor>> interceptors, InvocationHandler[] callbacks) {
            super();
            this.injectionPoint = injectionPoint;
            this.constructor = (Constructor) injectionPoint.getMember();
            this.enhancedConstructor = BytecodeGen.enhancedConstructor(enhancer, this.constructor);
            this.interceptors = interceptors;
            this.callbacks = callbacks;
        }

        public T newInstance(Object... arguments) throws InvocationTargetException {
            return (T) this.enhancedConstructor.apply(this.callbacks, arguments);
        }

        public InjectionPoint getInjectionPoint() {
            return this.injectionPoint;
        }

        public Constructor<T> getConstructor() {
            return this.constructor;
        }

        public ImmutableMap<Method, List<MethodInterceptor>> getMethodInterceptors() {
            return this.interceptors;
        }
    }
}
