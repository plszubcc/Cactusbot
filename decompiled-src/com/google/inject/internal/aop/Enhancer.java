package com.google.inject.internal.aop;

import com.google.inject.internal.asm.$ClassWriter;
import com.google.inject.internal.asm.$Handle;
import com.google.inject.internal.asm.$MethodVisitor;
import com.google.inject.internal.asm.$Type;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class Enhancer extends AbstractGlueGenerator {

    private static final String HANDLERS_NAME = "GUICE$HANDLERS";
    private static final String HANDLERS_DESCRIPTOR = "[Ljava/lang/reflect/InvocationHandler;";
    private static final String HANDLER_TYPE = $Type.getInternalName(InvocationHandler.class);
    private static final String HANDLER_ARRAY_TYPE = $Type.getInternalName(InvocationHandler[].class);
    private static final String INVOKERS_NAME = "GUICE$INVOKERS";
    private static final String INVOKERS_DESCRIPTOR = "Ljava/lang/invoke/MethodHandle;";
    private static final String CALLBACK_DESCRIPTOR = "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;";
    private static final String METAFACTORY_DESCRIPTOR = "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;";
    private static final $Type INDEX_TO_INVOKER_METHOD_TYPE = $Type.getMethodType("(I)Ljava/util/function/BiFunction;");
    private static final $Type RAW_INVOKER_METHOD_TYPE = $Type.getMethodType("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
    private static final $Type INVOKER_METHOD_TYPE = $Type.getMethodType("(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;");
    private final Map<Method, Method> bridgeDelegates;
    private final String checkcastToProxy;

    Enhancer(Class<?> hostClass, Map<Method, Method> bridgeDelegates) {
        super(hostClass, "$$EnhancerByGuice$$");
        this.bridgeDelegates = bridgeDelegates;
        this.checkcastToProxy = ClassDefining.canDowncastToProxy(hostClass) ? this.proxyName : this.hostName;
    }

    protected byte[] generateGlue(Collection<Executable> members) {
        $ClassWriter cw = new $ClassWriter(1);

        cw.visit(52, 33, this.proxyName, (String) null, this.hostName, (String[]) null);
        cw.visitSource("<generated>", (String) null);
        cw.visitField(25, "GUICE$INVOKERS", "Ljava/lang/invoke/MethodHandle;", (String) null, (Object) null).visitEnd();
        this.setupInvokerTable(cw);
        this.generateTrampoline(cw, members);
        cw.visitField(18, "GUICE$HANDLERS", "[Ljava/lang/reflect/InvocationHandler;", (String) null, (Object) null).visitEnd();
        Set<Method> remainingBridgeMethods = new HashSet(this.bridgeDelegates.keySet());
        int methodIndex = 0;

        for (Executable member : members) {
            if (member instanceof Constructor) {
                this.enhanceConstructor(cw, (Constructor) member);
            } else {
                this.enhanceMethod(cw, (Method) member, methodIndex++);
                remainingBridgeMethods.remove(member);
            }
        }

        for (Method method : remainingBridgeMethods) {
            Method target = (Method) this.bridgeDelegates.get(method);

            if (target != null) {
                this.generateVirtualBridge(cw, method, target);
            }
        }

        cw.visitEnd();
        return cw.toByteArray();
    }

    private void setupInvokerTable($ClassWriter cw) {
        $MethodVisitor mv = cw.visitMethod(10, "<clinit>", "()V", (String) null, (String[]) null);

        mv.visitCode();
        $Handle trampolineHandle = new $Handle(6, this.proxyName, "GUICE$TRAMPOLINE", "(ILjava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false);

        if (ClassDefining.canLoadProxyByName(this.hostClass)) {
            mv.visitMethodInsn(184, "java/lang/invoke/MethodHandles", "lookup", "()Ljava/lang/invoke/MethodHandles$Lookup;", false);
            mv.visitLdcInsn("apply");
            mv.visitLdcInsn(Enhancer.INDEX_TO_INVOKER_METHOD_TYPE);
            mv.visitLdcInsn(Enhancer.RAW_INVOKER_METHOD_TYPE);
            mv.visitLdcInsn(trampolineHandle);
            mv.visitLdcInsn(Enhancer.INVOKER_METHOD_TYPE);
            mv.visitMethodInsn(184, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false);
            mv.visitMethodInsn(182, "java/lang/invoke/CallSite", "getTarget", "()Ljava/lang/invoke/MethodHandle;", false);
        } else {
            mv.visitLdcInsn(trampolineHandle);
        }

        mv.visitFieldInsn(179, this.proxyName, "GUICE$INVOKERS", "Ljava/lang/invoke/MethodHandle;");
        mv.visitInsn(177);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private void enhanceConstructor($ClassWriter cw, Constructor<?> constructor) {
        String descriptor = $Type.getConstructorDescriptor(constructor);
        String enhancedDescriptor = "([Ljava/lang/reflect/InvocationHandler;" + descriptor.substring(1);
        $MethodVisitor mv = cw.visitMethod(1, "<init>", enhancedDescriptor, (String) null, exceptionNames(constructor));

        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitInsn(89);
        mv.visitVarInsn(25, 1);
        mv.visitFieldInsn(181, this.proxyName, "GUICE$HANDLERS", "[Ljava/lang/reflect/InvocationHandler;");
        int slot = 2;

        for (Class<?> parameterType : constructor.getParameterTypes()) {
            slot += BytecodeTasks.loadArgument(mv, parameterType, slot);
        }

        mv.visitMethodInsn(183, this.hostName, "<init>", descriptor, false);
        mv.visitInsn(177);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private void enhanceMethod($ClassWriter cw, Method method, int methodIndex) {
        $MethodVisitor mv = cw.visitMethod(16 | method.getModifiers() & -1313, method.getName(), $Type.getMethodDescriptor(method), (String) null, exceptionNames(method));

        mv.visitVarInsn(25, 0);
        mv.visitInsn(89);
        mv.visitFieldInsn(180, this.proxyName, "GUICE$HANDLERS", "[Ljava/lang/reflect/InvocationHandler;");
        BytecodeTasks.pushInteger(mv, methodIndex);
        mv.visitInsn(50);
        mv.visitInsn(95);
        mv.visitInsn(1);
        BytecodeTasks.packArguments(mv, method.getParameterTypes());
        mv.visitMethodInsn(185, Enhancer.HANDLER_TYPE, "invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;", true);
        Class<?> returnType = method.getReturnType();

        if (returnType == Void.TYPE) {
            mv.visitInsn(177);
        } else if (returnType.isPrimitive()) {
            $Type primitiveType = $Type.getType(returnType);

            BytecodeTasks.unbox(mv, primitiveType);
            mv.visitInsn(primitiveType.getOpcode(172));
        } else {
            mv.visitTypeInsn(192, $Type.getInternalName(returnType));
            mv.visitInsn(176);
        }

        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    protected void generateConstructorInvoker($MethodVisitor mv, Constructor<?> constructor) {
        String descriptor = $Type.getConstructorDescriptor(constructor);
        String enhancedDescriptor = "([Ljava/lang/reflect/InvocationHandler;" + descriptor.substring(1);

        mv.visitTypeInsn(187, this.proxyName);
        mv.visitInsn(89);
        mv.visitVarInsn(25, 1);
        mv.visitTypeInsn(192, Enhancer.HANDLER_ARRAY_TYPE);
        BytecodeTasks.unpackArguments(mv, constructor.getParameterTypes());
        mv.visitMethodInsn(183, this.proxyName, "<init>", enhancedDescriptor, false);
    }

    protected void generateMethodInvoker($MethodVisitor mv, Method method) {
        Method target = (Method) this.bridgeDelegates.getOrDefault(method, method);
        int invokeOpcode = target != method ? 182 : 183;

        mv.visitVarInsn(25, 1);
        mv.visitTypeInsn(192, this.checkcastToProxy);
        BytecodeTasks.unpackArguments(mv, target.getParameterTypes());
        mv.visitMethodInsn(invokeOpcode, this.hostName, target.getName(), $Type.getMethodDescriptor(target), false);
        Class<?> returnType = target.getReturnType();

        if (returnType == Void.TYPE) {
            mv.visitInsn(1);
        } else if (returnType.isPrimitive()) {
            BytecodeTasks.box(mv, $Type.getType(returnType));
        }

    }

    private void generateVirtualBridge($ClassWriter cw, Method bridge, Method target) {
        $MethodVisitor mv = cw.visitMethod(16 | bridge.getModifiers() & -1313, bridge.getName(), $Type.getMethodDescriptor(bridge), (String) null, exceptionNames(bridge));

        mv.visitVarInsn(25, 0);
        mv.visitTypeInsn(192, this.checkcastToProxy);
        Class<?>[] bridgeParameterTypes = bridge.getParameterTypes();
        Class<?>[] targetParameterTypes = target.getParameterTypes();
        int slot = 1;
        int i = 0;

        for (int len = targetParameterTypes.length; i < len; ++i) {
            Class<?> parameterType = targetParameterTypes[i];

            slot += BytecodeTasks.loadArgument(mv, parameterType, slot);
            if (parameterType != bridgeParameterTypes[i]) {
                mv.visitTypeInsn(192, $Type.getInternalName(parameterType));
            }
        }

        mv.visitMethodInsn(182, this.hostName, target.getName(), $Type.getMethodDescriptor(target), false);
        $Type returnType = $Type.getType(bridge.getReturnType());

        if (target.getReturnType() != bridge.getReturnType()) {
            mv.visitTypeInsn(192, returnType.getInternalName());
        }

        mv.visitInsn(returnType.getOpcode(172));
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    protected MethodHandle lookupInvokerTable(Class<?> glueClass) throws Throwable {
        return (MethodHandle) glueClass.getField("GUICE$INVOKERS").get((Object) null);
    }

    private static String[] exceptionNames(Executable member) {
        Class<?>[] exceptionClasses = member.getExceptionTypes();
        String[] exceptionNames = new String[exceptionClasses.length];

        Arrays.setAll(exceptionNames, (ix) -> {
            return $Type.getInternalName(exceptionClasses[ix]);
        });
        return exceptionNames;
    }
}
