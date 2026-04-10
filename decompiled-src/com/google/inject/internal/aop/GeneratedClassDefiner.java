package com.google.inject.internal.aop;

import java.util.function.BiFunction;

final class GeneratedClassDefiner implements ClassDefiner {

    private final BiFunction<ClassLoader, byte[], Class<?>> defineAccess;

    GeneratedClassDefiner(BiFunction<ClassLoader, byte[], Class<?>> defineAccess) {
        super();
        this.defineAccess = defineAccess;
    }

    public Class<?> define(Class<?> hostClass, byte[] bytecode) {
        return (Class) this.defineAccess.apply(hostClass.getClassLoader(), bytecode);
    }
}
