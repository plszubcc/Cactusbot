package com.google.inject.internal.aop;

import com.google.common.collect.ImmutableMap;
import com.google.inject.internal.BytecodeGen;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;

final class EnhancerBuilderImpl implements BytecodeGen.EnhancerBuilder {

    private static final ClassValue<Map<BitSet, Function<String, BiFunction<Object, Object[], Object>>>> ENHANCERS = new ClassValue<Map<BitSet, Function<String, BiFunction<Object, Object[], Object>>>>() {
        protected Map<BitSet, Function<String, BiFunction<Object, Object[], Object>>> computeValue(Class<?> hostClass) {
            return new HashMap();
        }
    };
    private final Class<?> hostClass;
    private final Method[] enhanceableMethods;
    private final Map<Method, Method> bridgeDelegates;

    EnhancerBuilderImpl(Class<?> hostClass, Collection<Method> enhanceableMethods, Map<Method, Method> bridgeDelegates) {
        super();
        this.hostClass = hostClass;
        this.enhanceableMethods = (Method[]) enhanceableMethods.toArray(new Method[0]);
        this.bridgeDelegates = ImmutableMap.copyOf(bridgeDelegates);
    }

    public Method[] getEnhanceableMethods() {
        return this.enhanceableMethods;
    }

    public Function<String, BiFunction<Object, Object[], Object>> buildEnhancer(BitSet methodIndices) {
        if ((this.hostClass.getModifiers() & 16) != 0) {
            throw new IllegalArgumentException("Cannot subclass final " + this.hostClass);
        } else {
            Map<BitSet, Function<String, BiFunction<Object, Object[], Object>>> enhancers = (Map) EnhancerBuilderImpl.ENHANCERS.get(this.hostClass);

            synchronized (enhancers) {
                return (Function) enhancers.computeIfAbsent(methodIndices, this::doBuildEnhancer);
            }
        }
    }

    private Function<String, BiFunction<Object, Object[], Object>> doBuildEnhancer(BitSet methodIndices) {
        NavigableMap<String, Executable> glueMap = new TreeMap();

        ClassBuilding.visitMembers(this.hostClass.getDeclaredConstructors(), ClassDefining.hasPackageAccess(), (ctorx) -> {
            glueMap.put(ClassBuilding.signature(ctorx), ctorx);
        });

        for (int methodIndex = methodIndices.nextSetBit(0); methodIndex >= 0; methodIndex = methodIndices.nextSetBit(methodIndex + 1)) {
            Method method = this.enhanceableMethods[methodIndex];

            glueMap.put(ClassBuilding.signature(method), method);
        }

        return (new Enhancer(this.hostClass, this.bridgeDelegates)).glue(glueMap);
    }
}
