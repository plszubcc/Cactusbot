package com.google.inject;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CheckReturnValue;
import com.google.inject.internal.Annotations;
import com.google.inject.internal.MoreTypes;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@CheckReturnValue
public class Key<T> {

    private final Key.AnnotationStrategy annotationStrategy;
    private final TypeLiteral<T> typeLiteral;
    private final int hashCode;
    private String toString;

    protected Key(Class<? extends Annotation> annotationType) {
        super();
        this.annotationStrategy = strategyFor(annotationType);
        this.typeLiteral = MoreTypes.<T>canonicalizeForKey(TypeLiteral.fromSuperclassTypeParameter(this.getClass()));
        this.hashCode = this.computeHashCode();
    }

    protected Key(Annotation annotation) {
        super();
        this.annotationStrategy = strategyFor(annotation);
        this.typeLiteral = MoreTypes.<T>canonicalizeForKey(TypeLiteral.fromSuperclassTypeParameter(this.getClass()));
        this.hashCode = this.computeHashCode();
    }

    protected Key() {
        super();
        this.annotationStrategy = Key.NullAnnotationStrategy.INSTANCE;
        this.typeLiteral = MoreTypes.<T>canonicalizeForKey(TypeLiteral.fromSuperclassTypeParameter(this.getClass()));
        this.hashCode = this.computeHashCode();
    }

    private Key(Type type, Key.AnnotationStrategy annotationStrategy) {
        super();
        this.annotationStrategy = annotationStrategy;
        this.typeLiteral = MoreTypes.<T>canonicalizeForKey(TypeLiteral.get(type));
        this.hashCode = this.computeHashCode();
    }

    private Key(TypeLiteral<T> typeLiteral, Key.AnnotationStrategy annotationStrategy) {
        super();
        this.annotationStrategy = annotationStrategy;
        this.typeLiteral = MoreTypes.<T>canonicalizeForKey(typeLiteral);
        this.hashCode = this.computeHashCode();
    }

    private int computeHashCode() {
        return this.typeLiteral.hashCode() * 31 + this.annotationStrategy.hashCode();
    }

    public final TypeLiteral<T> getTypeLiteral() {
        return this.typeLiteral;
    }

    public final Class<? extends Annotation> getAnnotationType() {
        return this.annotationStrategy.getAnnotationType();
    }

    public final Annotation getAnnotation() {
        return this.annotationStrategy.getAnnotation();
    }

    boolean hasAnnotationType() {
        return this.annotationStrategy.getAnnotationType() != null;
    }

    String getAnnotationName() {
        Annotation annotation = this.annotationStrategy.getAnnotation();

        return annotation != null ? annotation.toString() : this.annotationStrategy.getAnnotationType().toString();
    }

    Class<? super T> getRawType() {
        return this.typeLiteral.getRawType();
    }

    Key<Provider<T>> providerKey() {
        return this.ofType(this.typeLiteral.providerType());
    }

    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Key)) {
            return false;
        } else {
            Key<?> other = (Key) o;

            return this.annotationStrategy.equals(other.annotationStrategy) && this.typeLiteral.equals(other.typeLiteral);
        }
    }

    public final int hashCode() {
        return this.hashCode;
    }

    public final String toString() {
        String local = this.toString;

        if (local == null) {
            local = "Key[type=" + this.typeLiteral + ", annotation=" + this.annotationStrategy + ']';
            this.toString = local;
        }

        return local;
    }

    static <T> Key<T> get(Class<T> type, Key.AnnotationStrategy annotationStrategy) {
        return new Key<T>(type, annotationStrategy);
    }

    public static <T> Key<T> get(Class<T> type) {
        return new Key<T>(type, Key.NullAnnotationStrategy.INSTANCE);
    }

    public static <T> Key<T> get(Class<T> type, Class<? extends Annotation> annotationType) {
        return new Key<T>(type, strategyFor(annotationType));
    }

    public static <T> Key<T> get(Class<T> type, Annotation annotation) {
        return new Key<T>(type, strategyFor(annotation));
    }

    public static Key<?> get(Type type) {
        return new Key(type, Key.NullAnnotationStrategy.INSTANCE);
    }

    public static Key<?> get(Type type, Class<? extends Annotation> annotationType) {
        return new Key(type, strategyFor(annotationType));
    }

    public static Key<?> get(Type type, Annotation annotation) {
        return new Key(type, strategyFor(annotation));
    }

    public static <T> Key<T> get(TypeLiteral<T> typeLiteral) {
        return new Key<T>(typeLiteral, Key.NullAnnotationStrategy.INSTANCE);
    }

    public static <T> Key<T> get(TypeLiteral<T> typeLiteral, Class<? extends Annotation> annotationType) {
        return new Key<T>(typeLiteral, strategyFor(annotationType));
    }

    public static <T> Key<T> get(TypeLiteral<T> typeLiteral, Annotation annotation) {
        return new Key<T>(typeLiteral, strategyFor(annotation));
    }

    public <U> Key<U> ofType(Class<U> type) {
        return new Key<U>(type, this.annotationStrategy);
    }

    public Key<?> ofType(Type type) {
        return new Key(type, this.annotationStrategy);
    }

    public <U> Key<U> ofType(TypeLiteral<U> type) {
        return new Key<U>(type, this.annotationStrategy);
    }

    public Key<T> withAnnotation(Class<? extends Annotation> annotationType) {
        return new Key<T>(this.typeLiteral, strategyFor(annotationType));
    }

    public Key<T> withAnnotation(Annotation annotation) {
        return new Key<T>(this.typeLiteral, strategyFor(annotation));
    }

    public boolean hasAttributes() {
        return this.annotationStrategy.hasAttributes();
    }

    public Key<T> withoutAttributes() {
        return new Key<T>(this.typeLiteral, this.annotationStrategy.withoutAttributes());
    }

    static Key.AnnotationStrategy strategyFor(Annotation annotation) {
        Preconditions.checkNotNull(annotation, "annotation");
        Class<? extends Annotation> annotationType = annotation.annotationType();

        ensureRetainedAtRuntime(annotationType);
        ensureIsBindingAnnotation(annotationType);
        return (Key.AnnotationStrategy) (Annotations.isMarker(annotationType) ? new Key.AnnotationTypeStrategy(annotationType, annotation) : new Key.AnnotationInstanceStrategy(Annotations.canonicalizeIfNamed(annotation)));
    }

    static Key.AnnotationStrategy strategyFor(Class<? extends Annotation> annotationType) {
        annotationType = Annotations.canonicalizeIfNamed(annotationType);
        if (Annotations.isAllDefaultMethods(annotationType)) {
            return strategyFor(Annotations.generateAnnotation(annotationType));
        } else {
            Preconditions.checkNotNull(annotationType, "annotation type");
            ensureRetainedAtRuntime(annotationType);
            ensureIsBindingAnnotation(annotationType);
            return new Key.AnnotationTypeStrategy(annotationType, (Annotation) null);
        }
    }

    private static void ensureRetainedAtRuntime(Class<? extends Annotation> annotationType) {
        Preconditions.checkArgument(Annotations.isRetainedAtRuntime(annotationType), "%s is not retained at runtime. Please annotate it with @Retention(RUNTIME).", annotationType.getName());
    }

    private static void ensureIsBindingAnnotation(Class<? extends Annotation> annotationType) {
        Preconditions.checkArgument(Annotations.isBindingAnnotation(annotationType), "%s is not a binding annotation. Please annotate it with @BindingAnnotation.", annotationType.getName());
    }

    static enum NullAnnotationStrategy implements Key.AnnotationStrategy {

        INSTANCE;

        private NullAnnotationStrategy() {}

        public boolean hasAttributes() {
            return false;
        }

        public Key.AnnotationStrategy withoutAttributes() {
            throw new UnsupportedOperationException("Key already has no attributes.");
        }

        public Annotation getAnnotation() {
            return null;
        }

        public Class<? extends Annotation> getAnnotationType() {
            return null;
        }

        public String toString() {
            return "[none]";
        }
    }

    static class AnnotationInstanceStrategy implements Key.AnnotationStrategy {

        final Annotation annotation;

        AnnotationInstanceStrategy(Annotation annotation) {
            super();
            this.annotation = (Annotation) Preconditions.checkNotNull(annotation, "annotation");
        }

        public boolean hasAttributes() {
            return true;
        }

        public Key.AnnotationStrategy withoutAttributes() {
            return new Key.AnnotationTypeStrategy(this.getAnnotationType(), this.annotation);
        }

        public Annotation getAnnotation() {
            return this.annotation;
        }

        public Class<? extends Annotation> getAnnotationType() {
            return this.annotation.annotationType();
        }

        public boolean equals(Object o) {
            if (!(o instanceof Key.AnnotationInstanceStrategy)) {
                return false;
            } else {
                Key.AnnotationInstanceStrategy other = (Key.AnnotationInstanceStrategy) o;

                return this.annotation.equals(other.annotation);
            }
        }

        public int hashCode() {
            return this.annotation.hashCode();
        }

        public String toString() {
            return this.annotation.toString();
        }
    }

    static class AnnotationTypeStrategy implements Key.AnnotationStrategy {

        final Class<? extends Annotation> annotationType;
        final Annotation annotation;

        AnnotationTypeStrategy(Class<? extends Annotation> annotationType, Annotation annotation) {
            super();
            this.annotationType = (Class) Preconditions.checkNotNull(annotationType, "annotation type");
            this.annotation = annotation;
        }

        public boolean hasAttributes() {
            return false;
        }

        public Key.AnnotationStrategy withoutAttributes() {
            throw new UnsupportedOperationException("Key already has no attributes.");
        }

        public Annotation getAnnotation() {
            return this.annotation;
        }

        public Class<? extends Annotation> getAnnotationType() {
            return this.annotationType;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Key.AnnotationTypeStrategy)) {
                return false;
            } else {
                Key.AnnotationTypeStrategy other = (Key.AnnotationTypeStrategy) o;

                return this.annotationType.equals(other.annotationType);
            }
        }

        public int hashCode() {
            return this.annotationType.hashCode();
        }

        public String toString() {
            return Annotations.annotationInstanceClassString(this.annotationType, true);
        }
    }

    interface AnnotationStrategy {

        Annotation getAnnotation();

        Class<? extends Annotation> getAnnotationType();

        boolean hasAttributes();

        Key.AnnotationStrategy withoutAttributes();
    }
}
