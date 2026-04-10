package com.google.inject;

import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.AnnotatedConstantBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.Message;
import com.google.inject.spi.ModuleAnnotatedMethodScanner;
import com.google.inject.spi.ProvisionListener;
import com.google.inject.spi.TypeConverter;
import com.google.inject.spi.TypeListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;

public interface Binder {

    void bindInterceptor(Matcher<? super Class<?>> classMatcher, Matcher<? super Method> methodMatcher, MethodInterceptor... interceptors);

    void bindScope(Class<? extends Annotation> annotationType, Scope scope);

    <T> LinkedBindingBuilder<T> bind(Key<T> key);

    <T> AnnotatedBindingBuilder<T> bind(TypeLiteral<T> typeLiteral);

    <T> AnnotatedBindingBuilder<T> bind(Class<T> type);

    AnnotatedConstantBindingBuilder bindConstant();

    <T> void requestInjection(TypeLiteral<T> type, T instance);

    void requestInjection(Object instance);

    void requestStaticInjection(Class<?>... types);

    void install(Module module);

    Stage currentStage();

    void addError(String message, Object... arguments);

    void addError(Throwable t);

    void addError(Message message);

    <T> Provider<T> getProvider(Key<T> key);

    <T> Provider<T> getProvider(Dependency<T> dependency);

    <T> Provider<T> getProvider(Class<T> type);

    <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> typeLiteral);

    <T> MembersInjector<T> getMembersInjector(Class<T> type);

    void convertToTypes(Matcher<? super TypeLiteral<?>> typeMatcher, TypeConverter converter);

    void bindListener(Matcher<? super TypeLiteral<?>> typeMatcher, TypeListener listener);

    void bindListener(Matcher<? super Binding<?>> bindingMatcher, ProvisionListener... listeners);

    Binder withSource(Object source);

    Binder skipSources(Class<?>... classesToSkip);

    PrivateBinder newPrivateBinder();

    void requireExplicitBindings();

    void disableCircularProxies();

    void requireAtInjectOnConstructors();

    void requireExactBindingAnnotations();

    void scanModulesForAnnotatedMethods(ModuleAnnotatedMethodScanner scanner);
}
