package com.google.inject.internal;

import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.BindingAnnotation;
import com.google.inject.Key;
import com.google.inject.ScopeAnnotation;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.util.Classes;
import com.google.inject.name.Names;
import jakarta.inject.Named;
import jakarta.inject.Qualifier;
import jakarta.inject.Scope;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class Annotations {

    private static final LoadingCache<Class<? extends Annotation>, Annotation> cache = CacheBuilder.newBuilder().weakKeys().build(new CacheLoader<Class<? extends Annotation>, Annotation>() {
        public Annotation load(Class<? extends Annotation> input) {
            return Annotations.generateAnnotationImpl(input);
        }
    });
    private static final MapJoiner JOINER = Joiner.on(", ").withKeyValueSeparator("=");
    private static final Annotations.AnnotationToStringConfig ANNOTATION_TO_STRING_CONFIG = determineAnnotationToStringConfig();
    private static final Annotations.AnnotationChecker scopeChecker = new Annotations.AnnotationChecker(Arrays.asList(ScopeAnnotation.class, Scope.class));
    private static final Annotations.AnnotationChecker bindingAnnotationChecker = new Annotations.AnnotationChecker(Arrays.asList(BindingAnnotation.class, Qualifier.class));

    public Annotations() {
        super();
    }

    public static boolean isMarker(Class<? extends Annotation> annotationType) {
        return annotationType.getDeclaredMethods().length == 0;
    }

    public static boolean isAllDefaultMethods(Class<? extends Annotation> annotationType) {
        boolean hasMethods = false;

        for (Method m : annotationType.getDeclaredMethods()) {
            hasMethods = true;
            if (m.getDefaultValue() == null) {
                return false;
            }
        }

        return hasMethods;
    }

    public static <T extends Annotation> T generateAnnotation(Class<T> annotationType) {
        Preconditions.checkState(isAllDefaultMethods(annotationType), "%s is not all default methods", annotationType);
        return (T) (Annotations.cache.getUnchecked(annotationType));
    }

    private static <T extends Annotation> T generateAnnotationImpl(final Class<T> annotationType) {
        final Map<String, Object> members = resolveMembers(annotationType);

        return (T) (annotationType.cast(Proxy.newProxyInstance(annotationType.getClassLoader(), new Class[]{annotationType}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
                String name = method.getName();

                return name.equals("annotationType") ? annotationType : (name.equals("toString") ? Annotations.annotationToString(annotationType, members) : (name.equals("hashCode") ? Annotations.annotationHashCode(annotationType, members) : (name.equals("equals") ? Annotations.annotationEquals(annotationType, members, args[0]) : members.get(name))));
            }
        })));
    }

    private static ImmutableMap<String, Object> resolveMembers(Class<? extends Annotation> annotationType) {
        ImmutableMap.Builder<String, Object> result = ImmutableMap.builder();

        for (Method method : annotationType.getDeclaredMethods()) {
            result.put(method.getName(), method.getDefaultValue());
        }

        return result.buildOrThrow();
    }

    private static boolean annotationEquals(Class<? extends Annotation> type, Map<String, Object> members, Object other) throws Exception {
        if (!type.isInstance(other)) {
            return false;
        } else {
            for (Method method : type.getDeclaredMethods()) {
                String name = method.getName();

                if (!Arrays.deepEquals(new Object[]{method.invoke(other)}, new Object[]{members.get(name)})) {
                    return false;
                }
            }

            return true;
        }
    }

    private static int annotationHashCode(Class<? extends Annotation> type, Map<String, Object> members) throws Exception {
        int result = 0;

        for (Method method : type.getDeclaredMethods()) {
            String name = method.getName();
            Object value = members.get(name);

            result += 127 * name.hashCode() ^ Arrays.deepHashCode(new Object[]{value}) - 31;
        }

        return result;
    }

    private static String annotationToString(Class<? extends Annotation> type, Map<String, Object> members) throws Exception {
        StringBuilder sb = (new StringBuilder()).append('@').append(type.getName()).append('(');

        Annotations.JOINER.appendTo(sb, Maps.transformValues(members, (argx) -> {
            String s = Arrays.deepToString(new Object[]{argx});

            return s.substring(1, s.length() - 1);
        }));
        return sb.append(')').toString();
    }

    public static boolean isRetainedAtRuntime(Class<? extends Annotation> annotationType) {
        Retention retention = (Retention) annotationType.getAnnotation(Retention.class);

        return retention != null && retention.value() == RetentionPolicy.RUNTIME;
    }

    public static Class<? extends Annotation> findScopeAnnotation(Errors errors, Class<?> implementation) {
        return findScopeAnnotation(errors, implementation.getAnnotations());
    }

    public static Class<? extends Annotation> findScopeAnnotation(Errors errors, Annotation[] annotations) {
        Class<? extends Annotation> found = null;

        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();

            if (isScopeAnnotation(annotationType)) {
                if (found != null) {
                    errors.duplicateScopeAnnotations(found, annotationType);
                } else {
                    found = annotationType;
                }
            }
        }

        return found;
    }

    static boolean containsComponentAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getSimpleName().equals("Component")) {
                return true;
            }
        }

        return false;
    }

    public static String memberValueString(String value) {
        return Annotations.ANNOTATION_TO_STRING_CONFIG.quote ? "\"" + value + "\"" : value;
    }

    public static String memberValueString(String memberName, Object value) {
        StringBuilder sb = new StringBuilder();
        boolean quote = Annotations.ANNOTATION_TO_STRING_CONFIG.quote;
        boolean includeMemberName = Annotations.ANNOTATION_TO_STRING_CONFIG.includeMemberName;

        if (includeMemberName) {
            sb.append(memberName).append('=');
        }

        if (quote && value instanceof String) {
            sb.append('"').append(value).append('"');
        } else {
            sb.append(value);
        }

        return sb.toString();
    }

    public static String annotationInstanceClassString(Class<? extends Annotation> ann, boolean includePackage) {
        return Annotations.ANNOTATION_TO_STRING_CONFIG.dollarSeparator ? (!includePackage && ann.getPackage() != null ? "@" + ann.getName().substring(ann.getPackage().getName().length() + 1) : "@" + ann.getName()) : "@" + dotSeparatedParentOrPackage(ann, includePackage);
    }

    private static String dotSeparatedParentOrPackage(Class<?> ann, boolean includePackage) {
        return ann.getDeclaringClass() == null ? (includePackage ? ann.getPackage().getName() + "." + ann.getSimpleName() : ann.getSimpleName()) : dotSeparatedParentOrPackage(ann.getDeclaringClass(), includePackage) + "." + ann.getSimpleName();
    }

    @Annotations.TestAnnotation("determineAnnotationToStringConfig")
    private static Annotations.AnnotationToStringConfig determineAnnotationToStringConfig() {
        try {
            String annotation = ((Annotations.TestAnnotation) Annotations.class.getDeclaredMethod("determineAnnotationToStringConfig").getAnnotation(Annotations.TestAnnotation.class)).toString();
            boolean quote = annotation.contains("\"determineAnnotationToStringConfig\"");
            boolean includeMemberName = annotation.contains("value=");
            boolean dollarSeparator = annotation.contains("Annotations$TestAnnotation");

            return new Annotations.AnnotationToStringConfig(quote, includeMemberName, dollarSeparator);
        } catch (NoSuchMethodException nosuchmethodexception) {
            throw new AssertionError(nosuchmethodexception);
        }
    }

    public static boolean isScopeAnnotation(Class<? extends Annotation> annotationType) {
        return Annotations.scopeChecker.hasAnnotations(annotationType);
    }

    public static void checkForMisplacedScopeAnnotations(Class<?> type, Object source, Errors errors) {
        if (!Classes.isConcrete(type)) {
            Class<? extends Annotation> scopeAnnotation = findScopeAnnotation(errors, type);

            if (scopeAnnotation != null && !containsComponentAnnotation(type.getAnnotations())) {
                errors.withSource(type).scopeAnnotationOnAbstractType(scopeAnnotation, type, source);
            }

        }
    }

    public static Key<?> getKey(TypeLiteral<?> type, Member member, Annotation[] annotations, Errors errors) throws ErrorsException {
        int numErrorsBefore = errors.size();
        Annotation found = findBindingAnnotation(errors, member, annotations);

        errors.throwIfNewErrors(numErrorsBefore);
        return found == null ? Key.get(type) : Key.get(type, found);
    }

    public static Annotation findBindingAnnotation(Errors errors, Member member, Annotation[] annotations) {
        Annotation found = null;

        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();

            if (isBindingAnnotation(annotationType)) {
                if (found != null) {
                    errors.duplicateBindingAnnotations(member, found.annotationType(), annotationType);
                } else {
                    found = annotation;
                }
            }
        }

        return found;
    }

    public static boolean isBindingAnnotation(Class<? extends Annotation> annotationType) {
        return Annotations.bindingAnnotationChecker.hasAnnotations(annotationType);
    }

    public static Annotation canonicalizeIfNamed(Annotation annotation) {
        return (Annotation) (annotation instanceof Named ? Names.named(((Named) annotation).value()) : annotation);
    }

    public static Class<? extends Annotation> canonicalizeIfNamed(Class<? extends Annotation> annotationType) {
        return annotationType == Named.class ? com.google.inject.name.Named.class : annotationType;
    }

    public static String nameOf(Key<?> key) {
        Annotation annotation = key.getAnnotation();
        Class<? extends Annotation> annotationType = key.getAnnotationType();

        return annotation != null && !isMarker(annotationType) ? key.getAnnotation().toString() : (key.getAnnotationType() != null ? '@' + key.getAnnotationType().getName() : "");
    }

    private static class AnnotationToStringConfig {

        final boolean quote;
        final boolean includeMemberName;
        final boolean dollarSeparator;

        AnnotationToStringConfig(boolean quote, boolean includeMemberName, boolean dollarSeparator) {
            super();
            this.quote = quote;
            this.includeMemberName = includeMemberName;
            this.dollarSeparator = dollarSeparator;
        }
    }

    static class AnnotationChecker {

        private final Collection<Class<? extends Annotation>> annotationTypes;
        private CacheLoader<Class<? extends Annotation>, Boolean> hasAnnotations = new CacheLoader<Class<? extends Annotation>, Boolean>() {
            public Boolean load(Class<? extends Annotation> annotationType) {
                for (Annotation annotation : annotationType.getAnnotations()) {
                    if (AnnotationChecker.this.annotationTypes.contains(annotation.annotationType())) {
                        return true;
                    }
                }

                return false;
            }
        };
        final LoadingCache<Class<? extends Annotation>, Boolean> cache;

        AnnotationChecker(Collection<Class<? extends Annotation>> annotationTypes) {
            super();
            this.cache = CacheBuilder.newBuilder().weakKeys().build(this.hasAnnotations);
            this.annotationTypes = annotationTypes;
        }

        boolean hasAnnotations(Class<? extends Annotation> annotated) {
            return (Boolean) this.cache.getUnchecked(annotated);
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    private @interface TestAnnotation {

        String value();
    }
}
