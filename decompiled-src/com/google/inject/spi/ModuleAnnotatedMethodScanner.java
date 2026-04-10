package com.google.inject.spi;

import com.google.inject.Binder;
import com.google.inject.Key;
import java.lang.annotation.Annotation;
import java.util.Set;

public abstract class ModuleAnnotatedMethodScanner {

    public ModuleAnnotatedMethodScanner() {
        super();
    }

    public abstract Set<? extends Class<? extends Annotation>> annotationClasses();

    public abstract <T> Key<T> prepareMethod(Binder binder, Annotation annotation, Key<T> key, InjectionPoint injectionPoint);
}
