package com.google.inject.spi;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.Scope;
import com.google.inject.internal.Errors;
import java.lang.annotation.Annotation;

public final class ScopeBinding implements Element {

    private final Object source;
    private final Class<? extends Annotation> annotationType;
    private final Scope scope;

    ScopeBinding(Object source, Class<? extends Annotation> annotationType, Scope scope) {
        super();
        this.source = Preconditions.checkNotNull(source, "source");
        this.annotationType = (Class) Preconditions.checkNotNull(annotationType, "annotationType");
        this.scope = (Scope) Preconditions.checkNotNull(scope, "scope");
    }

    public Object getSource() {
        return this.source;
    }

    public Class<? extends Annotation> getAnnotationType() {
        return this.annotationType;
    }

    public Scope getScope() {
        return this.scope;
    }

    public <T> T acceptVisitor(ElementVisitor<T> visitor) {
        return (T) visitor.visit(this);
    }

    public void applyTo(Binder binder) {
        binder.withSource(this.getSource()).bindScope(this.annotationType, this.scope);
    }

    public String toString() {
        return MoreObjects.toStringHelper(ScopeBinding.class).add("annotationType", this.annotationType).add("scope", this.scope).add("source", Errors.convert(this.source)).toString();
    }
}
