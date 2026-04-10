package com.google.inject.internal;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.ExposedBinding;
import com.google.inject.spi.PrivateElements;
import java.util.Set;

final class ExposedBindingImpl<T> extends BindingImpl<T> implements ExposedBinding<T> {

    private final PrivateElements privateElements;

    ExposedBindingImpl(InjectorImpl injector, Object source, Key<T> key, InternalFactory<T> factory, PrivateElements privateElements) {
        super(injector, key, source, factory, Scoping.UNSCOPED);
        this.privateElements = privateElements;
    }

    public <V> V acceptTargetVisitor(BindingTargetVisitor<? super T, V> visitor) {
        return (V) visitor.visit(this);
    }

    public Set<Dependency<?>> getDependencies() {
        return ImmutableSet.of(Dependency.get(Key.get(Injector.class)));
    }

    public PrivateElements getPrivateElements() {
        return this.privateElements;
    }

    public String toString() {
        return MoreObjects.toStringHelper(ExposedBinding.class).add("key", this.getKey()).add("source", this.getSource()).add("privateElements", this.privateElements).toString();
    }

    public void applyTo(Binder binder) {
        throw new UnsupportedOperationException("This element represents a synthetic binding.");
    }
}
