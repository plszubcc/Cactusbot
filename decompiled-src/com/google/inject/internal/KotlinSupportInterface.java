package com.google.inject.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Predicate;

public interface KotlinSupportInterface {

    Annotation[] getAnnotations(Field field);

    boolean isNullable(Field field);

    Predicate<Integer> getIsParameterKotlinNullablePredicate(Constructor<?> constructor);

    Predicate<Integer> getIsParameterKotlinNullablePredicate(Method method);

    void checkConstructorParameterAnnotations(Constructor<?> constructor, Errors errors);

    boolean isLocalClass(Class<?> clazz);
}
