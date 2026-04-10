package com.google.inject.internal;

import com.google.inject.BindingAnnotation;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.atomic.AtomicInteger;

public class UniqueAnnotations {

    private static final AtomicInteger nextUniqueValue = new AtomicInteger(1);
    private static final String INTERNAL_INSTANCE_STRING = Annotations.annotationInstanceClassString(UniqueAnnotations.Internal.class, true);

    private UniqueAnnotations() {
        super();
    }

    public static Annotation create() {
        return create(UniqueAnnotations.nextUniqueValue.getAndIncrement());
    }

    static Annotation create(final int value) {
        return new UniqueAnnotations.Internal() {
            public int value() {
                return value;
            }

            public Class<? extends Annotation> annotationType() {
                return UniqueAnnotations.Internal.class;
            }

            public String toString() {
                return UniqueAnnotations.INTERNAL_INSTANCE_STRING + '(' + Annotations.memberValueString("value", value) + ')';
            }

            public boolean equals(Object o) {
                return o instanceof UniqueAnnotations.Internal && ((UniqueAnnotations.Internal) o).value() == this.value();
            }

            public int hashCode() {
                return 127 * "value".hashCode() ^ value;
            }
        };
    }

    @Retention(RetentionPolicy.RUNTIME)
    @BindingAnnotation
    @interface Internal {

        int value();
    }
}
