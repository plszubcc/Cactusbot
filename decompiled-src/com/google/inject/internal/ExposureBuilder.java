package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.binder.AnnotatedElementBuilder;
import java.lang.annotation.Annotation;

public class ExposureBuilder<T> implements AnnotatedElementBuilder {

    private final Binder binder;
    private final Object source;
    private Key<T> key;

    public ExposureBuilder(Binder binder, Object source, Key<T> key) {
        super();
        this.binder = binder;
        this.source = source;
        this.key = key;
    }

    protected void checkNotAnnotated() {
        if (this.key.getAnnotationType() != null) {
            this.binder.addError("More than one annotation is specified for this binding.");
        }

    }

    public void annotatedWith(Class<? extends Annotation> annotationType) {
        Preconditions.checkNotNull(annotationType, "annotationType");
        this.checkNotAnnotated();
        this.key = this.key.withAnnotation(annotationType);
    }

    public void annotatedWith(Annotation annotation) {
        Preconditions.checkNotNull(annotation, "annotation");
        this.checkNotAnnotated();
        this.key = this.key.withAnnotation(annotation);
    }

    public Key<?> getKey() {
        return this.key;
    }

    public Object getSource() {
        return this.source;
    }

    public String toString() {
        return "AnnotatedElementBuilder";
    }
}
