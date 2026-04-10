package com.google.inject.util;

import java.util.Optional;

public final class Enhanced {

    private Enhanced() {
        super();
    }

    public static boolean isEnhanced(Class<?> clazz) {
        return clazz.getSimpleName().contains("$$EnhancerByGuice$$");
    }

    public static <T> Optional<Class<? super T>> unenhancedClass(Class<T> clazz) {
        return isEnhanced(clazz) ? Optional.of(clazz.getSuperclass()) : Optional.empty();
    }
}
