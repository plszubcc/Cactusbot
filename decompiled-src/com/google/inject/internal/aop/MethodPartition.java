package com.google.inject.internal.aop;

import com.google.inject.TypeLiteral;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

final class MethodPartition {

    private final List<Method> candidates = new ArrayList();

    public MethodPartition(Method first, Method second) {
        super();
        this.candidates.add(first);
        this.candidates.add(second);
    }

    public MethodPartition addCandidate(Method method) {
        this.candidates.add(method);
        return this;
    }

    public void collectEnhanceableMethods(TypeLiteral<?> hostType, Consumer<Method> methodVisitor, Map<Method, Method> bridgeDelegates) {
        Map<String, Method> leafMethods = new HashMap();
        Map<String, Method> bridgeTargets = new HashMap();

        for (Method candidate : this.candidates) {
            String parametersKey = parametersKey(candidate.getParameterTypes());
            Method existingLeafMethod = (Method) leafMethods.putIfAbsent(parametersKey, candidate);

            if (existingLeafMethod == null) {
                if (candidate.isBridge()) {
                    bridgeTargets.put(parametersKey, (Object) null);
                }
            } else if (existingLeafMethod.isBridge()) {
                if (existingLeafMethod.getDeclaringClass() == candidate.getDeclaringClass()) {
                    if (!candidate.isBridge()) {
                        leafMethods.put(parametersKey, candidate);
                        bridgeTargets.remove(parametersKey);
                    } else if (existingLeafMethod.getReturnType().isAssignableFrom(candidate.getReturnType())) {
                        leafMethods.put(parametersKey, candidate);
                    }
                } else if (!candidate.isBridge()) {
                    bridgeTargets.putIfAbsent(parametersKey, candidate);
                }
            }
        }

        for (Map.Entry<String, Method> methodEntry : leafMethods.entrySet()) {
            Method method = (Method) methodEntry.getValue();

            if ((method.getModifiers() & 16) != 0) {
                bridgeTargets.remove(methodEntry.getKey());
            } else if (!method.isBridge()) {
                methodVisitor.accept(method);
            }
        }

        for (Map.Entry<String, Method> targetEntry : bridgeTargets.entrySet()) {
            Method originalBridge = (Method) leafMethods.get(targetEntry.getKey());
            Method superTarget = (Method) targetEntry.getValue();
            Method enhanceableMethod = originalBridge;

            for (Method candidate : this.candidates) {
                if (!candidate.isBridge()) {
                    boolean sameMethod = candidate == superTarget;

                    if (sameMethod) {
                        if (originalBridge.getReturnType() == superTarget.getReturnType() && !superTarget.getDeclaringClass().isInterface()) {
                            enhanceableMethod = superTarget;
                        }
                        break;
                    }

                    if (resolvedParametersMatch(originalBridge, hostType, candidate) || superTarget != null && resolvedParametersMatch(candidate, hostType, superTarget)) {
                        bridgeDelegates.put(originalBridge, candidate);
                        break;
                    }
                }
            }

            if ((enhanceableMethod.getModifiers() & 16) == 0) {
                methodVisitor.accept(enhanceableMethod);
            }
        }

    }

    private static String parametersKey(Class<?>[] parameterTypes) {
        StringBuilder key = new StringBuilder();
        int i = 0;

        for (int len = parameterTypes.length; i < len; ++i) {
            if (i > 0) {
                key.append(',');
            }

            key.append(parameterTypes[i].getName());
        }

        return key.toString();
    }

    private static boolean resolvedParametersMatch(Method subMethod, TypeLiteral<?> host, Method superMethod) {
        Class<?>[] parameterTypes = subMethod.getParameterTypes();
        List<TypeLiteral<?>> resolvedTypes = host.getParameterTypes(superMethod);
        int i = 0;

        for (int len = parameterTypes.length; i < len; ++i) {
            if (parameterTypes[i] != ((TypeLiteral) resolvedTypes.get(i)).getRawType()) {
                return false;
            }
        }

        return true;
    }
}
