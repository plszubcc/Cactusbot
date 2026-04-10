package com.google.inject.internal;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.Elements;
import com.google.inject.spi.UntargettedBinding;

final class UntargettedBindingImpl<T> extends BindingImpl<T> implements UntargettedBinding<T> {

    UntargettedBindingImpl(InjectorImpl injector, Key<T> key, Object source) {
        super(injector, key, source, new InternalFactory<T>() {
            public T get(InternalContext context, Dependency<?> dependency, boolean linked) {
                throw new AssertionError();
            }
        }, Scoping.UNSCOPED);
    }

    public UntargettedBindingImpl(Object source, Key<T> key, Scoping scoping) {
        super(source, key, scoping);
    }

    public <V> V acceptTargetVisitor(BindingTargetVisitor<? super T, V> visitor) {
        return (V) visitor.visit(this);
    }

    public BindingImpl<T> withScoping(Scoping scoping) {
        return new UntargettedBindingImpl<T>(this.getSource(), this.getKey(), scoping);
    }

    public BindingImpl<T> withKey(Key<T> key) {
        return new UntargettedBindingImpl<T>(this.getSource(), key, this.getScoping());
    }

    public void applyTo(Binder binder) {
        this.getScoping().applyTo(Elements.withTrustedSource(GuiceInternal.GUICE_INTERNAL, binder, this.getSource()).bind(this.getKey()));
    }

    public String toString() {
        return MoreObjects.toStringHelper(UntargettedBinding.class).add("key", this.getKey()).add("source", this.getSource()).toString();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof UntargettedBindingImpl)) {
            return false;
        } else {
            UntargettedBindingImpl<?> o = (UntargettedBindingImpl) obj;

            return this.getKey().equals(o.getKey()) && this.getScoping().equals(o.getScoping());
        }
    }

    public int hashCode() {
        return Objects.hashCode(new Object[]{this.getKey(), this.getScoping()});
    }
}
