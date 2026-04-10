package com.google.inject.internal.aop;

import com.google.inject.internal.asm.$ClassWriter;
import com.google.inject.internal.asm.$Label;
import com.google.inject.internal.asm.$MethodVisitor;
import com.google.inject.internal.asm.$Type;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.NavigableMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;

abstract class AbstractGlueGenerator {

    protected static final String GENERATED_SOURCE = "<generated>";
    protected static final String TRAMPOLINE_NAME = "GUICE$TRAMPOLINE";
    protected static final String TRAMPOLINE_DESCRIPTOR = "(ILjava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;";
    protected final Class<?> hostClass;
    protected final String hostName;
    protected final String proxyName;
    private static final AtomicInteger COUNTER = new AtomicInteger();

    protected AbstractGlueGenerator(Class<?> hostClass, String marker) {
        super();
        this.hostClass = hostClass;
        this.hostName = $Type.getInternalName(hostClass);
        this.proxyName = proxyName(this.hostName, marker, this.hashCode());
    }

    private static String proxyName(String hostName, String marker, int hash) {
        long id = (long) (hash & 1048575 | AbstractGlueGenerator.COUNTER.getAndIncrement() << 20);
        String proxyName = hostName + marker + Long.toHexString(id);

        if (proxyName.startsWith("java/") && !ClassDefining.hasPackageAccess()) {
            proxyName = '$' + proxyName;
        }

        return proxyName;
    }

    public final Function<String, BiFunction<Object, Object[], Object>> glue(NavigableMap<String, Executable> glueMap) {
        MethodHandle invokerTable;

        try {
            byte[] bytecode = this.generateGlue(glueMap.values());
            Class<?> glueClass = ClassDefining.define(this.hostClass, bytecode);

            invokerTable = this.lookupInvokerTable(glueClass);
        } catch (Throwable throwable) {
            throw new GlueException("Problem generating " + this.proxyName, throwable);
        }

        ToIntFunction<String> signatureTable = ImmutableStringTrie.buildTrie(glueMap.keySet());

        return bindSignaturesToInvokers(signatureTable, invokerTable);
    }

    protected abstract byte[] generateGlue(Collection<Executable> members);

    protected abstract MethodHandle lookupInvokerTable(Class<?> glueClass) throws Throwable;

    private static Function<String, BiFunction<Object, Object[], Object>> bindSignaturesToInvokers(ToIntFunction<String> signatureTable, MethodHandle invokerTable) {
        return invokerTable.type().parameterCount() == 1 ? (signaturex) -> {
            try {
                return invokerTable.invokeExact(signatureTable.applyAsInt(signaturex));
            } catch (Throwable throwable) {
                throw asIfUnchecked(throwable);
            }
        } : (signaturex) -> {
            int index = signatureTable.applyAsInt(signaturex);

            return (instancex, argumentsx) -> {
                try {
                    return invokerTable.invokeExact(index, instancex, argumentsx);
                } catch (Throwable throwable) {
                    throw asIfUnchecked(throwable);
                }
            };
        };
    }

    private static <E extends Throwable> RuntimeException asIfUnchecked(Throwable e) throws E {
        throw e;
    }

    protected final void generateTrampoline($ClassWriter cw, Collection<Executable> members) {
        $MethodVisitor mv = cw.visitMethod(9, "GUICE$TRAMPOLINE", "(ILjava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", (String) null, (String[]) null);

        mv.visitCode();
        $Label[] labels = new $Label[members.size()];

        Arrays.setAll(labels, (ix) -> {
            return new $Label();
        });
        $Label defaultLabel = new $Label();

        mv.visitVarInsn(21, 0);
        mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);
        int labelIndex = 0;

        for (Executable member : members) {
            mv.visitLabel(labels[labelIndex++]);
            mv.visitFrame(3, 0, (Object[]) null, 0, (Object[]) null);
            if (member instanceof Constructor) {
                this.generateConstructorInvoker(mv, (Constructor) member);
            } else {
                this.generateMethodInvoker(mv, (Method) member);
            }

            mv.visitInsn(176);
        }

        mv.visitLabel(defaultLabel);
        mv.visitFrame(3, 0, (Object[]) null, 0, (Object[]) null);
        mv.visitInsn(1);
        mv.visitInsn(176);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    protected abstract void generateConstructorInvoker($MethodVisitor mv, Constructor<?> constructor);

    protected abstract void generateMethodInvoker($MethodVisitor mv, Method method);
}
