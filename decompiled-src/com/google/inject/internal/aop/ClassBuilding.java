package com.google.inject.internal.aop;

import com.google.inject.TypeLiteral;
import com.google.inject.internal.BytecodeGen;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ClassBuilding {

    private static final Method[] OVERRIDABLE_OBJECT_METHODS = getOverridableObjectMethods();

    private ClassBuilding() {
        super();
    }

    public static String signature(Constructor<?> constructor) {
        return signature("<init>", constructor.getParameterTypes());
    }

    public static String signature(Method method) {
        return signature(method.getName(), method.getParameterTypes());
    }

    private static String signature(String name, Class<?>[] parameterTypes) {
        StringBuilder signature = new StringBuilder(name);

        for (Class<?> type : parameterTypes) {
            signature.append(';').append(type.getName());
        }

        return signature.toString();
    }

    public static boolean canEnhance(Executable member) {
        return canAccess(member, ClassDefining.hasPackageAccess());
    }

    public static BytecodeGen.EnhancerBuilder buildEnhancerBuilder(Class<?> hostClass) {
        Map<String, Object> methodPartitions = new HashMap();

        visitMethodHierarchy(hostClass, (methodx) -> {
            if ((methodx.getModifiers() & 8) == 0) {
                partitionMethod(methodx, methodPartitions);
            }

        });
        Map<String, Method> enhanceableMethods = new TreeMap();
        Map<Method, Method> bridgeDelegates = new HashMap();
        TypeLiteral<?> hostType = TypeLiteral.get(hostClass);

        for (Object partition : methodPartitions.values()) {
            if (partition instanceof Method) {
                Method method = (Method) partition;

                if ((method.getModifiers() & 16) == 0) {
                    enhanceableMethods.put(signature(method), method);
                }
            } else {
                ((MethodPartition) partition).collectEnhanceableMethods(hostType, (methodx) -> {
                    enhanceableMethods.put(signature(methodx), methodx);
                }, bridgeDelegates);
            }
        }

        return new EnhancerBuilderImpl(hostClass, enhanceableMethods.values(), bridgeDelegates);
    }

    private static void partitionMethod(Method method, Map<String, Object> partitions) {
        String partitionKey = method.getName() + '/' + method.getParameterCount();

        partitions.merge(partitionKey, method, ClassBuilding::mergeMethods);
    }

    private static Object mergeMethods(Object existing, Object added) {
        Method newMethod = (Method) added;

        return existing instanceof Method ? new MethodPartition((Method) existing, newMethod) : ((MethodPartition) existing).addCandidate(newMethod);
    }

    private static void visitMethodHierarchy(Class<?> hostClass, Consumer<Method> visitor) {
        Deque<Class<?>[]> interfaceStack = new ArrayDeque();
        String hostPackage = ClassDefining.hasPackageAccess() ? packageName(hostClass.getName()) : null;

        for (Class<?> clazz = hostClass; clazz != Object.class && clazz != null; clazz = clazz.getSuperclass()) {
            boolean samePackage = hostPackage != null && hostPackage.equals(packageName(clazz.getName()));

            visitMembers(clazz.getDeclaredMethods(), samePackage, visitor);
            pushInterfaces(interfaceStack, clazz.getInterfaces());
        }

        for (Method method : ClassBuilding.OVERRIDABLE_OBJECT_METHODS) {
            visitor.accept(method);
        }

        List<Class<?>> interfaces = new ArrayList();

        while (!interfaceStack.isEmpty()) {
            for (Class<?> intf : (Class[]) interfaceStack.pop()) {
                if (mergeInterface(interfaces, intf)) {
                    pushInterfaces(interfaceStack, intf.getInterfaces());
                }
            }
        }

        for (Class<?> intf : interfaces) {
            visitMembers(intf.getDeclaredMethods(), false, visitor);
        }

    }

    private static void pushInterfaces(Deque<Class<?>[]> interfaceStack, Class<?>[] interfaces) {
        if (interfaces.length > 0) {
            interfaceStack.push(interfaces);
        }

    }

    private static boolean mergeInterface(List<Class<?>> interfaces, Class<?> candidate) {
        int i = 0;

        for (int len = interfaces.size(); i < len; ++i) {
            Class<?> existingInterface = (Class) interfaces.get(i);

            if (existingInterface == candidate) {
                return false;
            }

            if (existingInterface.isAssignableFrom(candidate)) {
                interfaces.add(i, candidate);
                return true;
            }
        }

        return interfaces.add(candidate);
    }

    private static String packageName(String className) {
        return className.substring(0, className.lastIndexOf(46) + 1);
    }

    private static Method[] getOverridableObjectMethods() {
        List<Method> objectMethods = new ArrayList();

        visitMembers(Object.class.getDeclaredMethods(), false, (methodx) -> {
            if ((methodx.getModifiers() & 24) == 0 && !"finalize".equals(methodx.getName())) {
                objectMethods.add(methodx);
            }

        });
        return (Method[]) objectMethods.toArray(new Method[0]);
    }

    public static boolean canFastInvoke(Executable member) {
        int modifiers = member.getModifiers() & 3;

        if (ClassDefining.hasPackageAccess()) {
            return modifiers != 2;
        } else {
            boolean visible = modifiers == 1 && isPublic(member.getDeclaringClass());

            if (visible) {
                for (Class<?> type : member.getParameterTypes()) {
                    if (!isPublic(type)) {
                        return false;
                    }
                }
            }

            return visible;
        }
    }

    private static boolean isPublic(Class<?> clazz) {
        return (clazz.getModifiers() & 1) != 0;
    }

    public static Function<String, BiFunction<Object, Object[], Object>> buildFastClass(Class<?> hostClass) {
        NavigableMap<String, Executable> glueMap = new TreeMap();

        visitFastConstructors(hostClass, (ctorx) -> {
            glueMap.put(signature(ctorx), ctorx);
        });
        visitFastMethods(hostClass, (methodx) -> {
            glueMap.put(signature(methodx), methodx);
        });
        return (new FastClass(hostClass)).glue(glueMap);
    }

    private static void visitFastConstructors(Class<?> hostClass, Consumer<Constructor<?>> visitor) {
        if (ClassDefining.hasPackageAccess()) {
            visitMembers(hostClass.getDeclaredConstructors(), true, visitor);
        } else {
            for (Constructor<?> constructor : hostClass.getConstructors()) {
                visitor.accept(constructor);
            }
        }

    }

    private static void visitFastMethods(Class<?> hostClass, Consumer<Method> visitor) {
        if (ClassDefining.hasPackageAccess()) {
            visitMembers(hostClass.getDeclaredMethods(), true, visitor);
        } else {
            for (Method method : hostClass.getMethods()) {
                if (hostClass == method.getDeclaringClass()) {
                    visitor.accept(method);
                }
            }
        }

    }

    static <T extends Executable> void visitMembers(T[] members, boolean samePackage, Consumer<T> visitor) {
        for (T member : members) {
            if (canAccess(member, samePackage)) {
                visitor.accept(member);
            }
        }

    }

    private static boolean canAccess(Executable member, boolean samePackage) {
        int modifiers = member.getModifiers();

        return (modifiers & 5) != 0 || samePackage && (modifiers & 2) == 0;
    }
}
