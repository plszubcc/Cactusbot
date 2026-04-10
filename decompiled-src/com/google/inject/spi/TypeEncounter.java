package com.google.inject.spi;

import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matcher;
import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;

public interface TypeEncounter<I> {

    void addError(String message, Object... arguments);

    void addError(Throwable t);

    void addError(Message message);

    <T> Provider<T> getProvider(Key<T> key);

    <T> Provider<T> getProvider(Class<T> type);

    <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> typeLiteral);

    <T> MembersInjector<T> getMembersInjector(Class<T> type);

    void register(MembersInjector<? super I> membersInjector);

    void register(InjectionListener<? super I> listener);

    void bindInterceptor(Matcher<? super Method> methodMatcher, MethodInterceptor... interceptors);
}
