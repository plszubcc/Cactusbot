package com.google.inject.internal;

import java.lang.annotation.Annotation;

public class Nullability {

    private Nullability() {
        super();
    }

    public static boolean hasNullableAnnotation(Annotation[] annotations) {
        for (Annotation a : annotations) {
            Class<? extends Annotation> type = a.annotationType();

            if ("Nullable".equals(type.getSimpleName())) {
                return true;
            }
        }

        return false;
    }
}
