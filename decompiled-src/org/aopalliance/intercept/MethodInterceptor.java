package org.aopalliance.intercept;

public interface MethodInterceptor extends Interceptor {

    Object invoke(MethodInvocation methodinvocation) throws Throwable;
}
