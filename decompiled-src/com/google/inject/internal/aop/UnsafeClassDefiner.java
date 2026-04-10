package com.google.inject.internal.aop;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.internal.InternalFlags;
import com.google.inject.internal.asm.$ClassWriter;
import com.google.inject.internal.asm.$MethodVisitor;
import com.google.inject.internal.asm.$Type;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

final class UnsafeClassDefiner implements ClassDefiner {

    private static final Logger logger = Logger.getLogger(UnsafeClassDefiner.class.getName());
    private static final ClassDefiner UNSAFE_DEFINER;
    private static final boolean ALWAYS_DEFINE_ANONYMOUSLY;
    private static final String DEFINEACCESS_BY_GUICE_MARKER = "$$DefineAccessByGuice$$";
    private static final String[] DEFINEACCESS_API;
    private static final String CLASS_LOADER_TYPE;
    private static final String BYTE_ARRAY_TYPE;

    UnsafeClassDefiner() {
        super();
    }

    public static boolean isAccessible() {
        return UnsafeClassDefiner.UNSAFE_DEFINER != null;
    }

    public static boolean canLoadProxyByName(Class<?> hostClass) {
        return findClassDefiner(hostClass.getClassLoader()) != UnsafeClassDefiner.UNSAFE_DEFINER;
    }

    public static boolean canDowncastToProxy(Class<?> hostClass) {
        return !(findClassDefiner(hostClass.getClassLoader()) instanceof AnonymousClassDefiner);
    }

    public Class<?> define(Class<?> hostClass, byte[] bytecode) throws Exception {
        return findClassDefiner(hostClass.getClassLoader()).define(hostClass, bytecode);
    }

    private static ClassDefiner findClassDefiner(ClassLoader hostLoader) {
        return hostLoader != null && !UnsafeClassDefiner.ALWAYS_DEFINE_ANONYMOUSLY ? (UnsafeClassDefiner.ClassLoaderDefineClassHolder.CLASS_LOADER_DEFINE_CLASS != null ? UnsafeClassDefiner.ClassLoaderDefineClassHolder.CLASS_LOADER_DEFINE_CLASS : (ClassDefiner) UnsafeClassDefiner.DefineClassCacheHolder.DEFINE_CLASS_CACHE.getUnchecked(hostLoader.getClass())) : UnsafeClassDefiner.UNSAFE_DEFINER;
    }

    static <T> T tryPrivileged(PrivilegedExceptionAction<T> action, String errorMessage) {
        try {
            return (T) AccessController.doPrivileged(action);
        } catch (Throwable throwable) {
            UnsafeClassDefiner.logger.log(Level.FINE, errorMessage, throwable);
            return null;
        }
    }

    static ClassDefiner tryAccessDefineClass(Class<?> loaderClass) {
        try {
            UnsafeClassDefiner.logger.log(Level.FINE, "Accessing defineClass method in %s", loaderClass);
            return (ClassDefiner) AccessController.doPrivileged(() -> {
                return accessDefineClass(loaderClass);
            });
        } catch (Throwable throwable) {
            UnsafeClassDefiner.logger.log(Level.FINE, "Cannot access defineClass method in " + loaderClass, throwable);
            return UnsafeClassDefiner.UNSAFE_DEFINER;
        }
    }

    static ClassDefiner accessDefineClass(Class<?> loaderClass) throws Exception {
        byte[] bytecode = buildDefineClassAccess(loaderClass);
        Class<?> accessClass = UnsafeClassDefiner.UNSAFE_DEFINER.define(loaderClass, bytecode);

        return new GeneratedClassDefiner((BiFunction) accessClass.getDeclaredConstructor().newInstance());
    }

    private static byte[] buildDefineClassAccess(Class<?> loaderClass) {
        $ClassWriter cw = new $ClassWriter(1);

        cw.visit(52, 33, loaderClass.getName().replace('.', '/') + "$$DefineAccessByGuice$$", (String) null, "java/lang/Object", UnsafeClassDefiner.DEFINEACCESS_API);
        $MethodVisitor mv = cw.visitMethod(1, "<init>", "()V", (String) null, (String[]) null);

        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitMethodInsn(183, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(177);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        mv = cw.visitMethod(1, "apply", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", (String) null, (String[]) null);
        mv.visitCode();
        mv.visitVarInsn(25, 1);
        mv.visitTypeInsn(192, UnsafeClassDefiner.CLASS_LOADER_TYPE);
        mv.visitInsn(1);
        mv.visitVarInsn(25, 2);
        mv.visitTypeInsn(192, UnsafeClassDefiner.BYTE_ARRAY_TYPE);
        mv.visitInsn(3);
        mv.visitVarInsn(25, 2);
        mv.visitTypeInsn(192, UnsafeClassDefiner.BYTE_ARRAY_TYPE);
        mv.visitInsn(190);
        mv.visitMethodInsn(182, "java/lang/ClassLoader", "defineClass", "(Ljava/lang/String;[BII)Ljava/lang/Class;", false);
        mv.visitInsn(176);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();
        return cw.toByteArray();
    }

    static {
        ClassDefiner unsafeDefiner = (ClassDefiner) tryPrivileged(AnonymousClassDefiner::new, "Cannot bind Unsafe.defineAnonymousClass");

        if (unsafeDefiner == null) {
            unsafeDefiner = (ClassDefiner) tryPrivileged(HiddenClassDefiner::new, "Cannot bind MethodHandles.Lookup.defineHiddenClass");
        }

        UNSAFE_DEFINER = unsafeDefiner;
        ALWAYS_DEFINE_ANONYMOUSLY = InternalFlags.getCustomClassLoadingOption() == InternalFlags.CustomClassLoadingOption.ANONYMOUS;
        DEFINEACCESS_API = new String[]{"java/util/function/BiFunction"};
        CLASS_LOADER_TYPE = $Type.getInternalName(ClassLoader.class);
        BYTE_ARRAY_TYPE = $Type.getInternalName(byte[].class);
    }

    private static class ClassLoaderDefineClassHolder {

        static final ClassDefiner CLASS_LOADER_DEFINE_CLASS = (ClassDefiner) UnsafeClassDefiner.tryPrivileged(() -> {
            return UnsafeClassDefiner.accessDefineClass(ClassLoader.class);
        }, "Cannot access ClassLoader.defineClass");

        private ClassLoaderDefineClassHolder() {
            super();
        }
    }

    private static class DefineClassCacheHolder {

        static final LoadingCache<Class<?>, ClassDefiner> DEFINE_CLASS_CACHE = CacheBuilder.newBuilder().weakKeys().build(CacheLoader.from(UnsafeClassDefiner::tryAccessDefineClass));

        private DefineClassCacheHolder() {
            super();
        }
    }
}
