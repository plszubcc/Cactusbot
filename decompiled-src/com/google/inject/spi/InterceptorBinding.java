package com.google.inject.spi;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.matcher.Matcher;
import java.lang.reflect.Method;
import java.util.List;
import org.aopalliance.intercept.MethodInterceptor;

public final class InterceptorBinding implements Element {

    private final Object source;
    private final Matcher<? super Class<?>> classMatcher;
    private final Matcher<? super Method> methodMatcher;
    private final ImmutableList<MethodInterceptor> interceptors;

    InterceptorBinding(Object source, Matcher<? super Class<?>> classMatcher, Matcher<? super Method> methodMatcher, MethodInterceptor[] interceptors) {
        super();
        this.source = Preconditions.checkNotNull(source, "source");
        this.classMatcher = (Matcher) Preconditions.checkNotNull(classMatcher, "classMatcher");
        this.methodMatcher = (Matcher) Preconditions.checkNotNull(methodMatcher, "methodMatcher");
        this.interceptors = ImmutableList.copyOf(interceptors);
    }

    public Object getSource() {
        return this.source;
    }

    public Matcher<? super Class<?>> getClassMatcher() {
        return this.classMatcher;
    }

    public Matcher<? super Method> getMethodMatcher() {
        return this.methodMatcher;
    }

    public List<MethodInterceptor> getInterceptors() {
        return this.interceptors;
    }

    public <T> T acceptVisitor(ElementVisitor<T> visitor) {
        return (T) visitor.visit(this);
    }

    public void applyTo(Binder binder) {
        binder.withSource(this.getSource()).bindInterceptor(this.classMatcher, this.methodMatcher, (MethodInterceptor[]) this.interceptors.toArray(new MethodInterceptor[this.interceptors.size()]));
    }
}
