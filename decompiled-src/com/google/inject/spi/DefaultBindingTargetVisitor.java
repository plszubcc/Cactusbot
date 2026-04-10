package com.google.inject.spi;

import com.google.inject.Binding;

public abstract class DefaultBindingTargetVisitor<T, V> implements BindingTargetVisitor<T, V> {

    public DefaultBindingTargetVisitor() {
        super();
    }

    protected V visitOther(Binding<? extends T> binding) {
        return null;
    }

    public V visit(InstanceBinding<? extends T> instanceBinding) {
        return (V) this.visitOther(instanceBinding);
    }

    public V visit(ProviderInstanceBinding<? extends T> providerInstanceBinding) {
        return (V) this.visitOther(providerInstanceBinding);
    }

    public V visit(ProviderKeyBinding<? extends T> providerKeyBinding) {
        return (V) this.visitOther(providerKeyBinding);
    }

    public V visit(LinkedKeyBinding<? extends T> linkedKeyBinding) {
        return (V) this.visitOther(linkedKeyBinding);
    }

    public V visit(ExposedBinding<? extends T> exposedBinding) {
        return (V) this.visitOther(exposedBinding);
    }

    public V visit(UntargettedBinding<? extends T> untargettedBinding) {
        return (V) this.visitOther(untargettedBinding);
    }

    public V visit(ConstructorBinding<? extends T> constructorBinding) {
        return (V) this.visitOther(constructorBinding);
    }

    public V visit(ConvertedConstantBinding<? extends T> convertedConstantBinding) {
        return (V) this.visitOther(convertedConstantBinding);
    }

    public V visit(ProviderBinding<? extends T> providerBinding) {
        return (V) this.visitOther(providerBinding);
    }
}
