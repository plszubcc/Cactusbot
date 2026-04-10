package com.google.inject.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Predicate;

public final class KotlinSupport {

    private KotlinSupport() {
        super();
    }

    public static KotlinSupportInterface getInstance() {
        return KotlinSupport.KotlinSupportHolder.INSTANCE;
    }

    private static KotlinSupportInterface loadKotlinSupport() {
        try {
            Class<? extends KotlinSupportInterface> kotlinSupportClass = Class.forName("com.google.inject.KotlinSupportImpl");
            Field instance = kotlinSupportClass.getField("INSTANCE");

            instance.setAccessible(true);
            return (KotlinSupportInterface) instance.get((Object) null);
        } catch (ReflectiveOperationException reflectiveoperationexception) {
            return new KotlinSupport.KotlinUnsupported();
        }
    }

    private static class KotlinSupportHolder {

        static final KotlinSupportInterface INSTANCE = KotlinSupport.loadKotlinSupport();

        private KotlinSupportHolder() {
            super();
        }
    }

    private static class KotlinUnsupported implements KotlinSupportInterface {

        static final Annotation[] NO_ANNOTATIONS = new Annotation[0];
        static final Predicate<Integer> FALSE_PREDICATE = (integerx) -> {
            return false;
        };

        private KotlinUnsupported() {
            super();
        }

        public Annotation[] getAnnotations(Field field) {
            return KotlinSupport.KotlinUnsupported.NO_ANNOTATIONS;
        }

        public boolean isNullable(Field field) {
            return false;
        }

        public Predicate<Integer> getIsParameterKotlinNullablePredicate(Constructor<?> constructor) {
            return KotlinSupport.KotlinUnsupported.FALSE_PREDICATE;
        }

        public Predicate<Integer> getIsParameterKotlinNullablePredicate(Method method) {
            return KotlinSupport.KotlinUnsupported.FALSE_PREDICATE;
        }

        public void checkConstructorParameterAnnotations(Constructor<?> constructor, Errors errors) {}

        public boolean isLocalClass(Class<?> clazz) {
            return false;
        }
    }
}
