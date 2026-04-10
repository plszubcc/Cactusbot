package com.google.inject.internal;

import com.google.common.collect.Lists;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.BiFunction;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

final class InterceptorStackCallback implements InvocationHandler {

    private static final String GUICE_INTERNAL_AOP_PACKAGE = "com.google.inject.internal.aop";
    final Method method;
    final MethodInterceptor[] interceptors;
    final BiFunction<Object, Object[], Object> superInvoker;

    public InterceptorStackCallback(Method method, List<MethodInterceptor> interceptors, BiFunction<Object, Object[], Object> superInvoker) {
        super();
        this.method = method;
        this.interceptors = (MethodInterceptor[]) interceptors.toArray(new MethodInterceptor[interceptors.size()]);
        this.superInvoker = superInvoker;
    }

    public Object invoke(Object proxy, Method unused, Object[] arguments) throws Throwable {
        return (new InterceptorStackCallback.InterceptedMethodInvocation(proxy, arguments, 0)).proceed();
    }

    private void pruneStacktrace(Throwable throwable) {
        for (Throwable t = throwable; t != null; t = t.getCause()) {
            StackTraceElement[] stackTrace = t.getStackTrace();
            List<StackTraceElement> pruned = Lists.newArrayList();

            for (StackTraceElement element : stackTrace) {
                String className = element.getClassName();

                if (!className.startsWith(InterceptorStackCallback.class.getName()) && !className.startsWith("com.google.inject.internal.aop") && !className.contains("$$EnhancerByGuice$$")) {
                    pruned.add(element);
                }
            }

            t.setStackTrace((StackTraceElement[]) pruned.toArray(new StackTraceElement[pruned.size()]));
        }

    }

    private class InterceptedMethodInvocation implements MethodInvocation {

        final Object proxy;
        final Object[] arguments;
        final int interceptorIndex;

        public InterceptedMethodInvocation(Object proxy, Object[] arguments, int interceptorIndex) {
            super();
            this.proxy = proxy;
            this.arguments = arguments;
            this.interceptorIndex = interceptorIndex;
        }

        public Object proceed() throws Throwable {
            try {
                return this.interceptorIndex == InterceptorStackCallback.this.interceptors.length ? InterceptorStackCallback.this.superInvoker.apply(this.proxy, this.arguments) : InterceptorStackCallback.this.interceptors[this.interceptorIndex].invoke(InterceptorStackCallback.this.new InterceptedMethodInvocation(this.proxy, this.arguments, this.interceptorIndex + 1));
            } catch (Throwable throwable) {
                InterceptorStackCallback.this.pruneStacktrace(throwable);
                throw throwable;
            }
        }

        public Method getMethod() {
            return InterceptorStackCallback.this.method;
        }

        public Object[] getArguments() {
            return this.arguments;
        }

        public Object getThis() {
            return this.proxy;
        }

        public AccessibleObject getStaticPart() {
            return this.getMethod();
        }
    }
}
