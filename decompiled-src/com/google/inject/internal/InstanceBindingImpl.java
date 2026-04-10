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
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.InstanceBinding;
import java.util.Set;

final class InstanceBindingImpl<T> extends BindingImpl<T> implements InstanceBinding<T> {

    final T instance;
    final ImmutableSet<InjectionPoint> injectionPoints;

    public InstanceBindingImpl(InjectorImpl injector, Key<T> key, Object source, InternalFactory<? extends T> internalFactory, Set<InjectionPoint> injectionPoints, T instance) {
        super(injector, key, source, internalFactory, Scoping.EAGER_SINGLETON);
        this.injectionPoints = ImmutableSet.copyOf(injectionPoints);
        this.instance = instance;
    }

    public InstanceBindingImpl(Object source, Key<T> key, Scoping scoping, Set<InjectionPoint> injectionPoints, T instance) {
        super(source, key, scoping);
        this.injectionPoints = ImmutableSet.copyOf(injectionPoints);
        this.instance = instance;
    }

    public <V> V acceptTargetVisitor(BindingTargetVisitor<? super T, V> visitor) {
        return (V) visitor.visit(this);
    }

    public T getInstance() {
        return this.instance;
    }

    public Set<InjectionPoint> getInjectionPoints() {
        return this.injectionPoints;
    }

    public Set<Dependency<?>> getDependencies() {
        return (Set<Dependency<?>>) (this.instance instanceof HasDependencies ? ImmutableSet.copyOf(((HasDependencies) this.instance).getDependencies()) : Dependency.forInjectionPoints(this.injectionPoints));
    }

    public BindingImpl<T> withScoping(Scoping scoping) {
        return new InstanceBindingImpl<T>(this.getSource(), this.getKey(), scoping, this.injectionPoints, this.instance);
    }

    public BindingImpl<T> withKey(Key<T> key) {
        return new InstanceBindingImpl<T>(this.getSource(), key, this.getScoping(), this.injectionPoints, this.instance);
    }

    public void applyTo(Binder binder) {
        Elements.withTrustedSource(GuiceInternal.GUICE_INTERNAL, binder, this.getSource()).bind(this.getKey()).toInstance(this.instance);
    }

    public String toString() {
        return MoreObjects.toStringHelper(InstanceBinding.class).add("key", this.getKey()).add("source", this.getSource()).add("instance", this.instance).toString();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof InstanceBindingImpl)) {
            return false;
        } else {
            InstanceBindingImpl<?> o = (InstanceBindingImpl) obj;

            return this.getKey().equals(o.getKey()) && this.getScoping().equals(o.getScoping()) && Objects.equal(this.instance, o.instance);
        }
    }

    public int hashCode() {
        return Objects.hashCode(new Object[]{this.getKey(), this.getScoping()});
    }
}
