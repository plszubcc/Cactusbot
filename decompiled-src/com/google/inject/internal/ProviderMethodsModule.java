package com.google.inject.internal;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.Message;
import com.google.inject.spi.ModuleAnnotatedMethodScanner;
import com.google.inject.util.Modules;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ProviderMethodsModule implements Module {

    private final Object delegate;
    private final TypeLiteral<?> typeLiteral;
    private final boolean skipFastClassGeneration;
    private final ModuleAnnotatedMethodScanner scanner;

    private ProviderMethodsModule(Object delegate, boolean skipFastClassGeneration, ModuleAnnotatedMethodScanner scanner) {
        super();
        this.delegate = Preconditions.checkNotNull(delegate, "delegate");
        this.typeLiteral = TypeLiteral.get(this.getDelegateModuleClass());
        this.skipFastClassGeneration = skipFastClassGeneration;
        this.scanner = scanner;
    }

    public static Module forModule(Module module) {
        return forObject(module, false, ProvidesMethodScanner.INSTANCE);
    }

    public static Module forModule(Object module, ModuleAnnotatedMethodScanner scanner) {
        return forObject(module, false, scanner);
    }

    public static Module forObject(Object object) {
        return forObject(object, true, ProvidesMethodScanner.INSTANCE);
    }

    private static Module forObject(Object object, boolean skipFastClassGeneration, ModuleAnnotatedMethodScanner scanner) {
        return (Module) (object instanceof ProviderMethodsModule ? Modules.EMPTY_MODULE : new ProviderMethodsModule(object, skipFastClassGeneration, scanner));
    }

    public Class<?> getDelegateModuleClass() {
        return this.isStaticModule() ? (Class) this.delegate : this.delegate.getClass();
    }

    private boolean isStaticModule() {
        return this.delegate instanceof Class;
    }

    public void configure(Binder binder) {
        for (ProviderMethod<?> providerMethod : this.getProviderMethods(binder)) {
            providerMethod.configure(binder);
        }

    }

    public List<ProviderMethod<?>> getProviderMethods(Binder binder) {
        List<ProviderMethod<?>> result = null;
        List<ProviderMethodsModule.MethodAndAnnotation> methodsAndAnnotations = null;
        Class<?> superMostClass = this.getDelegateModuleClass();

        for (Class<?> c = superMostClass; c != Object.class && c != null; c = c.getSuperclass()) {
            for (Method method : DeclaredMembers.getDeclaredMethods(c)) {
                Annotation annotation = this.getAnnotation(binder, method);

                if (annotation != null) {
                    if (this.isStaticModule() && !Modifier.isStatic(method.getModifiers()) && !Modifier.isAbstract(method.getModifiers())) {
                        binder.addError("%s is an instance method, but a class literal was passed. Make this method static or pass an instance of the module instead.", method);
                    } else {
                        if (result == null) {
                            result = new ArrayList();
                            methodsAndAnnotations = new ArrayList();
                        }

                        ProviderMethod<Object> providerMethod = this.<Object>createProviderMethod(binder, method, annotation);

                        if (providerMethod != null) {
                            result.add(providerMethod);
                        }

                        methodsAndAnnotations.add(new ProviderMethodsModule.MethodAndAnnotation(method, annotation));
                        superMostClass = c;
                    }
                }
            }
        }

        if (result == null) {
            return ImmutableList.of();
        } else {
            Multimap<ProviderMethodsModule.Signature, Method> methodsBySignature = null;

            for (Class<?> c = this.getDelegateModuleClass(); c != superMostClass; c = c.getSuperclass()) {
                for (Method method : c.getDeclaredMethods()) {
                    if ((method.getModifiers() & 10) == 0 && !method.isBridge() && !method.isSynthetic()) {
                        if (methodsBySignature == null) {
                            methodsBySignature = HashMultimap.create();
                        }

                        methodsBySignature.put(new ProviderMethodsModule.Signature(this.typeLiteral, method), method);
                    }
                }
            }

            if (methodsBySignature != null) {
                for (ProviderMethodsModule.MethodAndAnnotation methodAndAnnotation : methodsAndAnnotations) {
                    Method method = methodAndAnnotation.method;
                    Annotation annotation = methodAndAnnotation.annotation;

                    for (Method matchingSignature : methodsBySignature.get(new ProviderMethodsModule.Signature(this.typeLiteral, method))) {
                        if (!matchingSignature.getDeclaringClass().isAssignableFrom(method.getDeclaringClass()) && overrides(matchingSignature, method)) {
                            String annotationString = annotation.annotationType() == Provides.class ? "@Provides" : "@" + annotation.annotationType().getCanonicalName();

                            binder.addError("Overriding " + annotationString + " methods is not allowed.\n\t" + annotationString + " method: %s\n\toverridden by: %s", method, matchingSignature);
                            break;
                        }
                    }
                }
            }

            return result;
        }
    }

    private Annotation getAnnotation(Binder binder, Method method) {
        if (!method.isBridge() && !method.isSynthetic()) {
            Annotation annotation = null;

            for (Class<? extends Annotation> annotationClass : this.scanner.annotationClasses()) {
                Annotation foundAnnotation = method.getAnnotation(annotationClass);

                if (foundAnnotation != null) {
                    if (annotation != null) {
                        binder.addError("More than one annotation claimed by %s on method %s. Methods can only have one annotation claimed per scanner.", this.scanner, method);
                        return null;
                    }

                    annotation = foundAnnotation;
                }
            }

            return annotation;
        } else {
            return null;
        }
    }

    private static boolean overrides(Method a, Method b) {
        int modifiers = b.getModifiers();

        return !Modifier.isPublic(modifiers) && !Modifier.isProtected(modifiers) ? (Modifier.isPrivate(modifiers) ? false : a.getDeclaringClass().getPackage().equals(b.getDeclaringClass().getPackage())) : true;
    }

    private <T> ProviderMethod<T> createProviderMethod(Binder binder, Method method, Annotation annotation) {
        binder = binder.withSource(method);
        Errors errors = new Errors(method);
        InjectionPoint point = InjectionPoint.forMethod(method, this.typeLiteral);
        TypeLiteral<T> returnType = this.typeLiteral.getReturnType(method);
        Key<T> key = this.<T>getKey(errors, returnType, method, method.getAnnotations());
        boolean prepareMethodError = false;

        try {
            key = this.scanner.<T>prepareMethod(binder, annotation, key, point);
        } catch (Throwable throwable) {
            prepareMethodError = true;
            binder.addError(throwable);
        }

        if (Modifier.isAbstract(method.getModifiers())) {
            Preconditions.checkState(prepareMethodError || key == null, "%s returned a non-null key (%s) for %s. prepareMethod() must return null for abstract methods", this.scanner, key, method);
            return null;
        } else if (key == null) {
            return null;
        } else {
            Class<? extends Annotation> scopeAnnotation = Annotations.findScopeAnnotation(errors, method.getAnnotations());

            for (Message message : errors.getMessages()) {
                binder.addError(message);
            }

            return ProviderMethod.<T>create(key, method, !this.isStaticModule() && !Modifier.isStatic(method.getModifiers()) ? this.delegate : null, ImmutableSet.copyOf(point.getDependencies()), scopeAnnotation, this.skipFastClassGeneration, annotation);
        }
    }

    <T> Key<T> getKey(Errors errors, TypeLiteral<T> type, Member member, Annotation[] annotations) {
        Annotation bindingAnnotation = Annotations.findBindingAnnotation(errors, member, annotations);

        return bindingAnnotation == null ? Key.get(type) : Key.get(type, bindingAnnotation);
    }

    public boolean equals(Object o) {
        return o instanceof ProviderMethodsModule && ((ProviderMethodsModule) o).delegate == this.delegate && ((ProviderMethodsModule) o).scanner.equals(this.scanner);
    }

    public int hashCode() {
        return Objects.hashCode(new Object[]{this.delegate, this.scanner});
    }

    public boolean isScanningBuiltInProvidesMethods() {
        return this.scanner == ProvidesMethodScanner.INSTANCE;
    }

    public ModuleAnnotatedMethodScanner getScanner() {
        return this.scanner;
    }

    private static class MethodAndAnnotation {

        final Method method;
        final Annotation annotation;

        MethodAndAnnotation(Method method, Annotation annotation) {
            super();
            this.method = method;
            this.annotation = annotation;
        }
    }

    private static final class Signature {

        final Class<?>[] parameters;
        final String name;
        final int hashCode;

        Signature(TypeLiteral<?> typeLiteral, Method method) {
            super();
            this.name = method.getName();
            List<TypeLiteral<?>> resolvedParameterTypes = typeLiteral.getParameterTypes(method);

            this.parameters = new Class[resolvedParameterTypes.size()];
            int i = 0;

            for (TypeLiteral<?> type : resolvedParameterTypes) {
                this.parameters[i] = type.getRawType();
            }

            this.hashCode = this.name.hashCode() + 31 * Arrays.hashCode(this.parameters);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof ProviderMethodsModule.Signature)) {
                return false;
            } else {
                ProviderMethodsModule.Signature other = (ProviderMethodsModule.Signature) obj;

                return other.name.equals(this.name) && Arrays.equals(this.parameters, other.parameters);
            }
        }

        public int hashCode() {
            return this.hashCode;
        }
    }
}
