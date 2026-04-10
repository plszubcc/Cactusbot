package com.google.inject.internal;

import com.google.inject.spi.InjectionPoint;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.BiFunction;

final class SingleMethodInjector implements SingleMemberInjector {

    private final InjectorImpl.MethodInvoker methodInvoker;
    private final SingleParameterInjector<?>[] parameterInjectors;
    private final InjectionPoint injectionPoint;

    SingleMethodInjector(InjectorImpl injector, InjectionPoint injectionPoint, Errors errors) throws ErrorsException {
        super();
        this.injectionPoint = injectionPoint;
        Method method = (Method) injectionPoint.getMember();

        this.methodInvoker = this.createMethodInvoker(method);
        this.parameterInjectors = injector.getParametersInjectors(injectionPoint.getDependencies(), errors);
    }

    private InjectorImpl.MethodInvoker createMethodInvoker(final Method method) {
        if (InternalFlags.isBytecodeGenEnabled()) {
            try {
                final BiFunction<Object, Object[], Object> fastMethod = BytecodeGen.fastMethod(method);

                if (fastMethod != null) {
                    return new InjectorImpl.MethodInvoker() {
                        public Object invoke(Object target, Object... parameters) throws InvocationTargetException {
                            try {
                                return fastMethod.apply(target, parameters);
                            } catch (Throwable throwable) {
                                throw new InvocationTargetException(throwable);
                            }
                        }
                    };
                }
            } catch (LinkageError | Exception exception) {
                ;
            }
        }

        int modifiers = method.getModifiers();

        if (!Modifier.isPublic(modifiers) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
            method.setAccessible(true);
        }

        return new InjectorImpl.MethodInvoker() {
            public Object invoke(Object target, Object... parameters) throws IllegalAccessException, InvocationTargetException {
                return method.invoke(target, parameters);
            }
        };
    }

    public InjectionPoint getInjectionPoint() {
        return this.injectionPoint;
    }

    public void inject(InternalContext context, Object o) throws InternalProvisionException {
        Object[] parameters = SingleParameterInjector.getAll(context, this.parameterInjectors);

        try {
            this.methodInvoker.invoke(o, parameters);
        } catch (IllegalAccessException illegalaccessexception) {
            throw new AssertionError(illegalaccessexception);
        } catch (InvocationTargetException invocationtargetexception) {
            Throwable cause = (Throwable) (invocationtargetexception.getCause() != null ? invocationtargetexception.getCause() : invocationtargetexception);

            throw InternalProvisionException.errorInjectingMethod(cause).addSource(this.injectionPoint);
        }
    }
}
