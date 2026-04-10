package com.google.inject.spi;

import com.google.inject.Scope;
import java.lang.annotation.Annotation;

public class DefaultBindingScopingVisitor<V> implements BindingScopingVisitor<V> {

    public DefaultBindingScopingVisitor() {
        super();
    }

    protected V visitOther() {
        return null;
    }

    public V visitEagerSingleton() {
        return (V) this.visitOther();
    }

    public V visitScope(Scope scope) {
        return (V) this.visitOther();
    }

    public V visitScopeAnnotation(Class<? extends Annotation> scopeAnnotation) {
        return (V) this.visitOther();
    }

    public V visitNoScoping() {
        return (V) this.visitOther();
    }
}
