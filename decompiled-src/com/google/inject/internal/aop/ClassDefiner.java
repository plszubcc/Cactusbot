package com.google.inject.internal.aop;

interface ClassDefiner {

    Class<?> define(Class<?> hostClass, byte[] bytecode) throws Exception;
}
