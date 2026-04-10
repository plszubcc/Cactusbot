package com.google.inject.spi;

import com.google.inject.Binding;

public interface ElementVisitor<V> {

    <T> V visit(Binding<T> binding);

    V visit(InterceptorBinding binding);

    V visit(ScopeBinding binding);

    V visit(TypeConverterBinding binding);

    V visit(InjectionRequest<?> request);

    V visit(StaticInjectionRequest request);

    <T> V visit(ProviderLookup<T> lookup);

    <T> V visit(MembersInjectorLookup<T> lookup);

    V visit(Message message);

    V visit(PrivateElements elements);

    V visit(TypeListenerBinding binding);

    V visit(ProvisionListenerBinding binding);

    V visit(RequireExplicitBindingsOption option);

    V visit(DisableCircularProxiesOption option);

    V visit(RequireAtInjectOnConstructorsOption option);

    V visit(RequireExactBindingAnnotationsOption option);

    V visit(ModuleAnnotatedMethodScannerBinding binding);
}
