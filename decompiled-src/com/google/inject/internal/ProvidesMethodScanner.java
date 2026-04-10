package com.google.inject.internal;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapKey;
import com.google.inject.multibindings.ProvidesIntoMap;
import com.google.inject.multibindings.ProvidesIntoOptional;
import com.google.inject.multibindings.ProvidesIntoSet;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.ModuleAnnotatedMethodScanner;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

final class ProvidesMethodScanner extends ModuleAnnotatedMethodScanner {

    static final ProvidesMethodScanner INSTANCE = new ProvidesMethodScanner();
    private static final ImmutableSet<Class<? extends Annotation>> ANNOTATIONS = ImmutableSet.of(Provides.class, ProvidesIntoSet.class, ProvidesIntoMap.class, ProvidesIntoOptional.class);

    private ProvidesMethodScanner() {
        super();
    }

    public Set<? extends Class<? extends Annotation>> annotationClasses() {
        return ProvidesMethodScanner.ANNOTATIONS;
    }

    public <T> Key<T> prepareMethod(Binder binder, Annotation annotation, Key<T> key, InjectionPoint injectionPoint) {
        Method method = (Method) injectionPoint.getMember();
        ProvidesMethodScanner.AnnotationOrError mapKey = findMapKeyAnnotation(binder, method);

        if (annotation instanceof Provides) {
            if (mapKey.annotation != null) {
                binder.addError("Found a MapKey annotation on non map binding at %s.", method);
            }

            return key;
        } else if (annotation instanceof ProvidesIntoSet) {
            if (mapKey.annotation != null) {
                binder.addError("Found a MapKey annotation on non map binding at %s.", method);
            }

            return RealMultibinder.<T>newRealSetBinder(binder, key).getKeyForNewItem();
        } else if (annotation instanceof ProvidesIntoMap) {
            if (mapKey.error) {
                return key;
            } else if (mapKey.annotation == null) {
                binder.addError("No MapKey found for map binding at %s.", method);
                return key;
            } else {
                ProvidesMethodScanner.TypeAndValue typeAndValue = typeAndValueOfMapKey(mapKey.annotation);

                return RealMapBinder.newRealMapBinder(binder, typeAndValue.type, key).getKeyForNewValue(typeAndValue.value);
            }
        } else {
            if (annotation instanceof ProvidesIntoOptional) {
                if (mapKey.annotation != null) {
                    binder.addError("Found a MapKey annotation on non map binding at %s.", method);
                }

                switch (((ProvidesIntoOptional) annotation).value()) {
                    case DEFAULT:
                        return RealOptionalBinder.<T>newRealOptionalBinder(binder, key).getKeyForDefaultBinding();
                    case ACTUAL:
                        return RealOptionalBinder.<T>newRealOptionalBinder(binder, key).getKeyForActualBinding();
                }
            }

            throw new IllegalStateException("Invalid annotation: " + annotation);
        }
    }

    private static ProvidesMethodScanner.AnnotationOrError findMapKeyAnnotation(Binder binder, Method method) {
        Annotation foundAnnotation = null;

        for (Annotation annotation : method.getAnnotations()) {
            MapKey mapKey = (MapKey) annotation.annotationType().getAnnotation(MapKey.class);

            if (mapKey != null) {
                if (foundAnnotation != null) {
                    binder.addError("Found more than one MapKey annotations on %s.", method);
                    return ProvidesMethodScanner.AnnotationOrError.forError();
                }

                if (mapKey.unwrapValue()) {
                    try {
                        Method valueMethod = annotation.annotationType().getDeclaredMethod("value");

                        if (valueMethod.getReturnType().isArray()) {
                            binder.addError("Array types are not allowed in a MapKey with unwrapValue=true: %s", annotation.annotationType());
                            return ProvidesMethodScanner.AnnotationOrError.forError();
                        }
                    } catch (NoSuchMethodException nosuchmethodexception) {
                        binder.addError("No 'value' method in MapKey with unwrapValue=true: %s", annotation.annotationType());
                        return ProvidesMethodScanner.AnnotationOrError.forError();
                    }
                }

                foundAnnotation = annotation;
            }
        }

        return ProvidesMethodScanner.AnnotationOrError.forPossiblyNullAnnotation(foundAnnotation);
    }

    static ProvidesMethodScanner.TypeAndValue<?> typeAndValueOfMapKey(Annotation mapKeyAnnotation) {
        if (!((MapKey) mapKeyAnnotation.annotationType().getAnnotation(MapKey.class)).unwrapValue()) {
            return new ProvidesMethodScanner.TypeAndValue(TypeLiteral.get(mapKeyAnnotation.annotationType()), mapKeyAnnotation);
        } else {
            try {
                Method valueMethod = mapKeyAnnotation.annotationType().getDeclaredMethod("value");

                valueMethod.setAccessible(true);
                TypeLiteral<?> returnType = TypeLiteral.get(mapKeyAnnotation.annotationType()).getReturnType(valueMethod);

                return new ProvidesMethodScanner.TypeAndValue(returnType, valueMethod.invoke(mapKeyAnnotation));
            } catch (NoSuchMethodException nosuchmethodexception) {
                throw new IllegalStateException(nosuchmethodexception);
            } catch (SecurityException securityexception) {
                throw new IllegalStateException(securityexception);
            } catch (IllegalAccessException illegalaccessexception) {
                throw new IllegalStateException(illegalaccessexception);
            } catch (InvocationTargetException invocationtargetexception) {
                throw new IllegalStateException(invocationtargetexception);
            }
        }
    }

    private static class AnnotationOrError {

        final Annotation annotation;
        final boolean error;

        AnnotationOrError(Annotation annotation, boolean error) {
            super();
            this.annotation = annotation;
            this.error = error;
        }

        static ProvidesMethodScanner.AnnotationOrError forPossiblyNullAnnotation(Annotation annotation) {
            return new ProvidesMethodScanner.AnnotationOrError(annotation, false);
        }

        static ProvidesMethodScanner.AnnotationOrError forError() {
            return new ProvidesMethodScanner.AnnotationOrError((Annotation) null, true);
        }
    }

    private static class TypeAndValue<T> {

        final TypeLiteral<T> type;
        final T value;

        TypeAndValue(TypeLiteral<T> type, T value) {
            super();
            this.type = type;
            this.value = value;
        }
    }
}
