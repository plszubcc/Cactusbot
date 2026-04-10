package com.google.inject;

import com.google.inject.spi.Element;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.TypeConverterBinding;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Injector {

    void injectMembers(Object instance);

    <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> typeLiteral);

    <T> MembersInjector<T> getMembersInjector(Class<T> type);

    Map<Key<?>, Binding<?>> getBindings();

    Map<Key<?>, Binding<?>> getAllBindings();

    <T> Binding<T> getBinding(Key<T> key);

    <T> Binding<T> getBinding(Class<T> type);

    <T> Binding<T> getExistingBinding(Key<T> key);

    <T> List<Binding<T>> findBindingsByType(TypeLiteral<T> type);

    <T> Provider<T> getProvider(Key<T> key);

    <T> Provider<T> getProvider(Class<T> type);

    <T> T getInstance(Key<T> key);

    <T> T getInstance(Class<T> type);

    Injector getParent();

    Injector createChildInjector(Iterable<? extends Module> modules);

    Injector createChildInjector(Module... modules);

    Map<Class<? extends Annotation>, Scope> getScopeBindings();

    Set<TypeConverterBinding> getTypeConverterBindings();

    List<Element> getElements();

    Map<TypeLiteral<?>, List<InjectionPoint>> getAllMembersInjectorInjectionPoints();
}
