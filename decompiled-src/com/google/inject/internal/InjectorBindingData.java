package com.google.inject.internal;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Scope;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionRequest;
import com.google.inject.spi.InterceptorBinding;
import com.google.inject.spi.MembersInjectorLookup;
import com.google.inject.spi.ModuleAnnotatedMethodScannerBinding;
import com.google.inject.spi.ProviderLookup;
import com.google.inject.spi.ProvisionListenerBinding;
import com.google.inject.spi.ScopeBinding;
import com.google.inject.spi.StaticInjectionRequest;
import com.google.inject.spi.TypeConverterBinding;
import com.google.inject.spi.TypeListenerBinding;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

class InjectorBindingData {

    private final Optional<InjectorBindingData> parent;
    private final Map<Key<?>, Binding<?>> explicitBindingsMutable = Maps.newLinkedHashMap();
    private final Map<Key<?>, Binding<?>> explicitBindings;
    private final Map<Class<? extends Annotation>, ScopeBinding> scopes;
    private final Set<ProviderLookup<?>> providerLookups;
    private final Set<StaticInjectionRequest> staticInjectionRequests;
    private final Set<MembersInjectorLookup<?>> membersInjectorLookups;
    private final Set<InjectionRequest<?>> injectionRequests;
    private final List<TypeConverterBinding> converters;
    private final List<InterceptorBinding> interceptorBindings;
    private final List<TypeListenerBinding> typeListenerBindings;
    private final List<ProvisionListenerBinding> provisionListenerBindings;
    private final List<ModuleAnnotatedMethodScannerBinding> scannerBindings;
    private final ListMultimap<TypeLiteral<?>, Binding<?>> indexedExplicitBindings;

    InjectorBindingData(Optional<InjectorBindingData> parent) {
        super();
        this.explicitBindings = Collections.unmodifiableMap(this.explicitBindingsMutable);
        this.scopes = Maps.newHashMap();
        this.providerLookups = Sets.newLinkedHashSet();
        this.staticInjectionRequests = Sets.newLinkedHashSet();
        this.membersInjectorLookups = Sets.newLinkedHashSet();
        this.injectionRequests = Sets.newLinkedHashSet();
        this.converters = Lists.newArrayList();
        this.interceptorBindings = Lists.newArrayList();
        this.typeListenerBindings = Lists.newArrayList();
        this.provisionListenerBindings = Lists.newArrayList();
        this.scannerBindings = Lists.newArrayList();
        this.indexedExplicitBindings = ArrayListMultimap.create();
        this.parent = parent;
    }

    public Optional<InjectorBindingData> parent() {
        return this.parent;
    }

    public <T> BindingImpl<T> getExplicitBinding(Key<T> key) {
        Binding<?> binding = (Binding) this.explicitBindings.get(key);

        return binding == null && this.parent.isPresent() ? ((InjectorBindingData) this.parent.get()).getExplicitBinding(key) : (BindingImpl) binding;
    }

    public Map<Key<?>, Binding<?>> getExplicitBindingsThisLevel() {
        return this.explicitBindings;
    }

    public void putBinding(Key<?> key, BindingImpl<?> binding) {
        this.explicitBindingsMutable.put(key, binding);
    }

    public void putProviderLookup(ProviderLookup<?> lookup) {
        this.providerLookups.add(lookup);
    }

    public Set<ProviderLookup<?>> getProviderLookupsThisLevel() {
        return this.providerLookups;
    }

    public void putStaticInjectionRequest(StaticInjectionRequest staticInjectionRequest) {
        this.staticInjectionRequests.add(staticInjectionRequest);
    }

    public Set<StaticInjectionRequest> getStaticInjectionRequestsThisLevel() {
        return this.staticInjectionRequests;
    }

    public void putInjectionRequest(InjectionRequest<?> injectionRequest) {
        this.injectionRequests.add(injectionRequest);
    }

    public Set<InjectionRequest<?>> getInjectionRequestsThisLevel() {
        return this.injectionRequests;
    }

    public void putMembersInjectorLookup(MembersInjectorLookup<?> membersInjectorLookup) {
        this.membersInjectorLookups.add(membersInjectorLookup);
    }

    public Set<MembersInjectorLookup<?>> getMembersInjectorLookupsThisLevel() {
        return this.membersInjectorLookups;
    }

    public ScopeBinding getScopeBinding(Class<? extends Annotation> annotationType) {
        ScopeBinding scopeBinding = (ScopeBinding) this.scopes.get(annotationType);

        return scopeBinding == null && this.parent.isPresent() ? ((InjectorBindingData) this.parent.get()).getScopeBinding(annotationType) : scopeBinding;
    }

    public void putScopeBinding(Class<? extends Annotation> annotationType, ScopeBinding scope) {
        this.scopes.put(annotationType, scope);
    }

    public Collection<ScopeBinding> getScopeBindingsThisLevel() {
        return this.scopes.values();
    }

    public Iterable<TypeConverterBinding> getConvertersThisLevel() {
        return this.converters;
    }

    public void addConverter(TypeConverterBinding typeConverterBinding) {
        this.converters.add(typeConverterBinding);
    }

    public TypeConverterBinding getConverter(String stringValue, TypeLiteral<?> type, Errors errors, Object source) {
        TypeConverterBinding matchingConverter = null;

        for (InjectorBindingData b = this; b != null; b = (InjectorBindingData) b.parent().orElse((Object) null)) {
            for (TypeConverterBinding converter : b.getConvertersThisLevel()) {
                if (converter.getTypeMatcher().matches(type)) {
                    if (matchingConverter != null) {
                        errors.ambiguousTypeConversion(stringValue, source, type, matchingConverter, converter);
                    }

                    matchingConverter = converter;
                }
            }
        }

        return matchingConverter;
    }

    public void addInterceptorBinding(InterceptorBinding interceptorBinding) {
        this.interceptorBindings.add(interceptorBinding);
    }

    public ImmutableList<InterceptorBinding> getInterceptorBindings() {
        return this.parent.isPresent() ? (new Builder()).addAll(((InjectorBindingData) this.parent.get()).getInterceptorBindings()).addAll(this.interceptorBindings).build() : ImmutableList.copyOf(this.interceptorBindings);
    }

    public ImmutableList<InterceptorBinding> getInterceptorBindingsThisLevel() {
        return ImmutableList.copyOf(this.interceptorBindings);
    }

    public void addTypeListener(TypeListenerBinding listenerBinding) {
        this.typeListenerBindings.add(listenerBinding);
    }

    public ImmutableList<TypeListenerBinding> getTypeListenerBindings() {
        return this.parent.isPresent() ? (new Builder()).addAll(((InjectorBindingData) this.parent.get()).getTypeListenerBindings()).addAll(this.typeListenerBindings).build() : ImmutableList.copyOf(this.typeListenerBindings);
    }

    public ImmutableList<TypeListenerBinding> getTypeListenerBindingsThisLevel() {
        return ImmutableList.copyOf(this.typeListenerBindings);
    }

    public void addProvisionListener(ProvisionListenerBinding listenerBinding) {
        this.provisionListenerBindings.add(listenerBinding);
    }

    public ImmutableList<ProvisionListenerBinding> getProvisionListenerBindings() {
        return this.parent.isPresent() ? (new Builder()).addAll(((InjectorBindingData) this.parent.get()).getProvisionListenerBindings()).addAll(this.provisionListenerBindings).build() : ImmutableList.copyOf(this.provisionListenerBindings);
    }

    public ImmutableList<ProvisionListenerBinding> getProvisionListenerBindingsThisLevel() {
        return ImmutableList.copyOf(this.provisionListenerBindings);
    }

    public void addScanner(ModuleAnnotatedMethodScannerBinding scanner) {
        this.scannerBindings.add(scanner);
    }

    public ImmutableList<ModuleAnnotatedMethodScannerBinding> getScannerBindings() {
        return this.parent.isPresent() ? (new Builder()).addAll(((InjectorBindingData) this.parent.get()).getScannerBindings()).addAll(this.scannerBindings).build() : ImmutableList.copyOf(this.scannerBindings);
    }

    public ImmutableList<ModuleAnnotatedMethodScannerBinding> getScannerBindingsThisLevel() {
        return ImmutableList.copyOf(this.scannerBindings);
    }

    public Map<Class<? extends Annotation>, Scope> getScopes() {
        ImmutableMap.Builder<Class<? extends Annotation>, Scope> builder = ImmutableMap.builder();

        for (Map.Entry<Class<? extends Annotation>, ScopeBinding> entry : this.scopes.entrySet()) {
            builder.put((Class) entry.getKey(), ((ScopeBinding) entry.getValue()).getScope());
        }

        return builder.buildOrThrow();
    }

    void indexBindingsByType() {
        for (Binding<?> binding : this.getExplicitBindingsThisLevel().values()) {
            this.indexedExplicitBindings.put(binding.getKey().getTypeLiteral(), binding);
        }

    }

    public ListMultimap<TypeLiteral<?>, Binding<?>> getIndexedExplicitBindings() {
        return this.indexedExplicitBindings;
    }
}
