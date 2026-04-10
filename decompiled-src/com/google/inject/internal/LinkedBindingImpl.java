package com.google.inject.internal;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.Elements;
import com.google.inject.spi.HasDependencies;
import com.google.inject.spi.LinkedKeyBinding;
import java.util.Set;

final class LinkedBindingImpl<T> extends BindingImpl<T> implements LinkedKeyBinding<T>, HasDependencies {

    final Key<? extends T> targetKey;

    LinkedBindingImpl(InjectorImpl injector, Key<T> key, Object source, InternalFactory<? extends T> internalFactory, Scoping scoping, Key<? extends T> targetKey) {
        super(injector, key, source, internalFactory, scoping);
        this.targetKey = targetKey;
    }

    LinkedBindingImpl(Object source, Key<T> key, Scoping scoping, Key<? extends T> targetKey) {
        super(source, key, scoping);
        this.targetKey = targetKey;
    }

    public <V> V acceptTargetVisitor(BindingTargetVisitor<? super T, V> visitor) {
        return (V) visitor.visit(this);
    }

    public Key<? extends T> getLinkedKey() {
        return this.targetKey;
    }

    public Set<Dependency<?>> getDependencies() {
        return ImmutableSet.of(Dependency.get(this.targetKey));
    }

    public BindingImpl<T> withScoping(Scoping scoping) {
        return new LinkedBindingImpl<T>(this.getSource(), this.getKey(), scoping, this.targetKey);
    }

    public BindingImpl<T> withKey(Key<T> key) {
        return new LinkedBindingImpl<T>(this.getSource(), key, this.getScoping(), this.targetKey);
    }

    public void applyTo(Binder binder) {
        this.getScoping().applyTo(Elements.withTrustedSource(GuiceInternal.GUICE_INTERNAL, binder, this.getSource()).bind(this.getKey()).to(this.getLinkedKey()));
    }

    public String toString() {
        return MoreObjects.toStringHelper(LinkedKeyBinding.class).add("key", this.getKey()).add("source", this.getSource()).add("scope", this.getScoping()).add("target", this.targetKey).toString();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof LinkedBindingImpl)) {
            return false;
        } else {
            LinkedBindingImpl<?> o = (LinkedBindingImpl) obj;

            return this.getKey().equals(o.getKey()) && this.getScoping().equals(o.getScoping()) && Objects.equal(this.targetKey, o.targetKey);
        }
    }

    public int hashCode() {
        return Objects.hashCode(new Object[]{this.getKey(), this.getScoping(), this.targetKey});
    }
}
