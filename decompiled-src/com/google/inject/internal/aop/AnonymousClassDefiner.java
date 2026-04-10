package com.google.inject.internal.aop;

import java.lang.reflect.Method;
import sun.misc.Unsafe;

final class AnonymousClassDefiner implements ClassDefiner {

    private static final Unsafe THE_UNSAFE;
    private static final Method ANONYMOUS_DEFINE_METHOD;

    AnonymousClassDefiner() {
        super();
    }

    public Class<?> define(Class<?> hostClass, byte[] bytecode) throws Exception {
        return (Class) AnonymousClassDefiner.ANONYMOUS_DEFINE_METHOD.invoke(AnonymousClassDefiner.THE_UNSAFE, hostClass, bytecode, null);
    }

    static {
        try {
            THE_UNSAFE = UnsafeGetter.getUnsafe();
            ANONYMOUS_DEFINE_METHOD = Unsafe.class.getMethod("defineAnonymousClass", Class.class, byte[].class, Object[].class);
        } catch (ReflectiveOperationException reflectiveoperationexception) {
            throw new ExceptionInInitializerError(reflectiveoperationexception);
        }
    }
}
