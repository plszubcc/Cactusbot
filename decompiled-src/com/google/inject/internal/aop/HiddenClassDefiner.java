package com.google.inject.internal.aop;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import sun.misc.Unsafe;

final class HiddenClassDefiner implements ClassDefiner {

    private static final Unsafe THE_UNSAFE;
    private static final Object TRUSTED_LOOKUP_BASE;
    private static final long TRUSTED_LOOKUP_OFFSET;
    private static final Object HIDDEN_CLASS_OPTIONS;
    private static final Method HIDDEN_DEFINE_METHOD;

    HiddenClassDefiner() {
        super();
    }

    public Class<?> define(Class<?> hostClass, byte[] bytecode) throws Exception {
        Lookup trustedLookup = (Lookup) HiddenClassDefiner.THE_UNSAFE.getObject(HiddenClassDefiner.TRUSTED_LOOKUP_BASE, HiddenClassDefiner.TRUSTED_LOOKUP_OFFSET);
        Lookup definedLookup = (Lookup) HiddenClassDefiner.HIDDEN_DEFINE_METHOD.invoke(trustedLookup.in(hostClass), bytecode, false, HiddenClassDefiner.HIDDEN_CLASS_OPTIONS);

        return definedLookup.lookupClass();
    }

    private static Object classOptions(String... options) throws ClassNotFoundException {
        Class optionClass = Class.forName(Lookup.class.getName() + "$ClassOption");
        Object classOptions = Array.newInstance(optionClass, options.length);

        for (int i = 0; i < options.length; ++i) {
            Array.set(classOptions, i, Enum.valueOf(optionClass, options[i]));
        }

        return classOptions;
    }

    static {
        try {
            THE_UNSAFE = UnsafeGetter.getUnsafe();
            Field trustedLookupField = Lookup.class.getDeclaredField("IMPL_LOOKUP");

            TRUSTED_LOOKUP_BASE = HiddenClassDefiner.THE_UNSAFE.staticFieldBase(trustedLookupField);
            TRUSTED_LOOKUP_OFFSET = HiddenClassDefiner.THE_UNSAFE.staticFieldOffset(trustedLookupField);
            HIDDEN_CLASS_OPTIONS = classOptions("NESTMATE");
            HIDDEN_DEFINE_METHOD = Lookup.class.getMethod("defineHiddenClass", byte[].class, Boolean.TYPE, HiddenClassDefiner.HIDDEN_CLASS_OPTIONS.getClass());
        } catch (ReflectiveOperationException reflectiveoperationexception) {
            throw new ExceptionInInitializerError(reflectiveoperationexception);
        }
    }
}
