package com.google.inject.internal.aop;

import com.google.inject.internal.InternalFlags;
import java.util.logging.Logger;

public final class ClassDefining {

    private static final Logger logger = Logger.getLogger(ClassDefining.class.getName());
    private static final String CLASS_DEFINING_UNSUPPORTED = "Unsafe is not accessible and custom classloading is turned OFF.";

    private ClassDefining() {
        super();
    }

    public static Class<?> define(Class<?> hostClass, byte[] bytecode) throws Exception {
        return ClassDefining.ClassDefinerHolder.INSTANCE.define(hostClass, bytecode);
    }

    public static boolean hasPackageAccess() {
        return ClassDefining.ClassDefinerHolder.IS_UNSAFE;
    }

    public static boolean canLoadProxyByName(Class<?> hostClass) {
        return !ClassDefining.ClassDefinerHolder.IS_UNSAFE || UnsafeClassDefiner.canLoadProxyByName(hostClass);
    }

    public static boolean canDowncastToProxy(Class<?> hostClass) {
        return !ClassDefining.ClassDefinerHolder.IS_UNSAFE || UnsafeClassDefiner.canDowncastToProxy(hostClass);
    }

    static ClassDefiner bindClassDefiner() {
        InternalFlags.CustomClassLoadingOption loadingOption = InternalFlags.getCustomClassLoadingOption();

        if (loadingOption == InternalFlags.CustomClassLoadingOption.CHILD) {
            return new ChildClassDefiner();
        } else if (UnsafeClassDefiner.isAccessible()) {
            return new UnsafeClassDefiner();
        } else if (loadingOption != InternalFlags.CustomClassLoadingOption.OFF) {
            return new ChildClassDefiner();
        } else {
            ClassDefining.logger.warning("Unsafe is not accessible and custom classloading is turned OFF.");
            return (hostClassx, bytecodex) -> {
                throw new UnsupportedOperationException("Cannot define class, Unsafe is not accessible and custom classloading is turned OFF.");
            };
        }
    }

    private static class ClassDefinerHolder {

        static final ClassDefiner INSTANCE = ClassDefining.bindClassDefiner();
        static final boolean IS_UNSAFE = ClassDefining.ClassDefinerHolder.INSTANCE instanceof UnsafeClassDefiner;

        private ClassDefinerHolder() {
            super();
        }
    }
}
