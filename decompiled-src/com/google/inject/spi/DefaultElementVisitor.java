package com.google.inject.spi;

import com.google.inject.Binding;

public abstract class DefaultElementVisitor<V> implements ElementVisitor<V> {

    public DefaultElementVisitor() {
        super();
    }

    protected V visitOther(Element element) {
        return null;
    }

    public V visit(Message message) {
        return (V) this.visitOther(message);
    }

    public <T> V visit(Binding<T> binding) {
        return (V) this.visitOther(binding);
    }

    public V visit(InterceptorBinding interceptorBinding) {
        return (V) this.visitOther(interceptorBinding);
    }

    public V visit(ScopeBinding scopeBinding) {
        return (V) this.visitOther(scopeBinding);
    }

    public V visit(TypeConverterBinding typeConverterBinding) {
        return (V) this.visitOther(typeConverterBinding);
    }

    public <T> V visit(ProviderLookup<T> providerLookup) {
        return (V) this.visitOther(providerLookup);
    }

    public V visit(InjectionRequest<?> injectionRequest) {
        return (V) this.visitOther(injectionRequest);
    }

    public V visit(StaticInjectionRequest staticInjectionRequest) {
        return (V) this.visitOther(staticInjectionRequest);
    }

    public V visit(PrivateElements privateElements) {
        return (V) this.visitOther(privateElements);
    }

    public <T> V visit(MembersInjectorLookup<T> lookup) {
        return (V) this.visitOther(lookup);
    }

    public V visit(TypeListenerBinding binding) {
        return (V) this.visitOther(binding);
    }

    public V visit(ProvisionListenerBinding binding) {
        return (V) this.visitOther(binding);
    }

    public V visit(DisableCircularProxiesOption option) {
        return (V) this.visitOther(option);
    }

    public V visit(RequireExplicitBindingsOption option) {
        return (V) this.visitOther(option);
    }

    public V visit(RequireAtInjectOnConstructorsOption option) {
        return (V) this.visitOther(option);
    }

    public V visit(RequireExactBindingAnnotationsOption option) {
        return (V) this.visitOther(option);
    }

    public V visit(ModuleAnnotatedMethodScannerBinding binding) {
        return (V) this.visitOther(binding);
    }
}
