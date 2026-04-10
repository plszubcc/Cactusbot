package com.google.inject.internal.aop;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.logging.Logger;

final class ChildClassDefiner implements ClassDefiner {

    private static final Logger logger = Logger.getLogger(ChildClassDefiner.class.getName());

    ChildClassDefiner() {
        super();
    }

    public Class<?> define(Class<?> hostClass, byte[] bytecode) throws Exception {
        ClassLoader hostLoader = hostClass.getClassLoader();
        ChildClassDefiner.ChildLoader childLoader = hostLoader != null ? (ChildClassDefiner.ChildLoader) ChildClassDefiner.ChildLoaderCacheHolder.CHILD_LOADER_CACHE.get(hostLoader) : ChildClassDefiner.SystemChildLoaderHolder.SYSTEM_CHILD_LOADER;

        return childLoader.defineInChild(bytecode);
    }

    static <T> T doPrivileged(PrivilegedAction<T> action) {
        return (T) AccessController.doPrivileged(action);
    }

    static ChildClassDefiner.ChildLoader childLoader(ClassLoader hostLoader) {
        ChildClassDefiner.logger.fine("Creating a child loader for " + hostLoader);
        return (ChildClassDefiner.ChildLoader) doPrivileged(() -> {
            return new ChildClassDefiner.ChildLoader(hostLoader);
        });
    }

    private static class SystemChildLoaderHolder {

        static final ChildClassDefiner.ChildLoader SYSTEM_CHILD_LOADER = (ChildClassDefiner.ChildLoader) ChildClassDefiner.doPrivileged(ChildClassDefiner.ChildLoader::new);

        private SystemChildLoaderHolder() {
            super();
        }
    }

    private static class ChildLoaderCacheHolder {

        static final LoadingCache<ClassLoader, ChildClassDefiner.ChildLoader> CHILD_LOADER_CACHE = CacheBuilder.newBuilder().weakKeys().weakValues().build(CacheLoader.from(ChildClassDefiner::childLoader));

        private ChildLoaderCacheHolder() {
            super();
        }
    }

    private static final class ChildLoader extends ClassLoader {

        ChildLoader(ClassLoader parent) {
            super(parent);
        }

        ChildLoader() {
            super();
        }

        Class<?> defineInChild(byte[] bytecode) {
            Class<?> type = this.defineClass((String) null, bytecode, 0, bytecode.length, (ProtectionDomain) null);

            this.resolveClass(type);
            return type;
        }
    }
}
