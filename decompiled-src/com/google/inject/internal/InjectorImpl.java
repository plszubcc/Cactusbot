package com.google.inject.internal;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.ConfigurationException;
import com.google.inject.ImplementedBy;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.ProvidedBy;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.util.SourceProvider;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.ConvertedConstantBinding;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.HasDependencies;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.InstanceBinding;
import com.google.inject.spi.ProviderBinding;
import com.google.inject.spi.TypeConverterBinding;
import com.google.inject.util.Providers;
import java.lang.annotation.Annotation;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

final class InjectorImpl implements Injector, Lookups {

    public static final TypeLiteral<String> STRING_TYPE = TypeLiteral.get(String.class);
    private final InjectorBindingData bindingData;
    private final InjectorJitBindingData jitBindingData;
    final InjectorImpl parent;
    final InjectorImpl.InjectorOptions options;
    Lookups lookups = new DeferredLookups(this);
    final Set<TypeLiteral<?>> userRequestedMembersInjectorTypes = Sets.newConcurrentHashSet();
    final ConstructorInjectorStore constructors = new ConstructorInjectorStore(this);
    MembersInjectorStore membersInjectorStore;
    ProvisionListenerCallbackStore provisionListenerStore;
    private final ThreadLocal<Object[]> localContext;

    InjectorImpl(InjectorImpl parent, InjectorBindingData bindingData, InjectorJitBindingData jitBindingData, InjectorImpl.InjectorOptions injectorOptions) {
        super();
        this.parent = parent;
        this.bindingData = bindingData;
        this.jitBindingData = jitBindingData;
        this.options = injectorOptions;
        if (parent != null) {
            this.localContext = parent.localContext;
        } else {
            this.localContext = new ThreadLocal();
        }

    }

    public <T> List<Binding<T>> findBindingsByType(TypeLiteral<T> type) {
        List<Binding<T>> list = this.bindingData.getIndexedExplicitBindings().get((TypeLiteral) Preconditions.checkNotNull(type, "type"));

        return Collections.unmodifiableList(list);
    }

    public <T> BindingImpl<T> getBinding(Key<T> key) {
        Errors errors = new Errors(Preconditions.checkNotNull(key, "key"));

        try {
            BindingImpl<T> result = this.<T>getBindingOrThrow(key, errors, InjectorImpl.JitLimitation.EXISTING_JIT);

            errors.throwConfigurationExceptionIfErrorsExist();
            return result;
        } catch (ErrorsException errorsexception) {
            ConfigurationException exception = new ConfigurationException(errors.merge(errorsexception.getErrors()).getMessages());

            throw exception;
        }
    }

    public <T> BindingImpl<T> getExistingBinding(Key<T> key) {
        BindingImpl<T> explicitBinding = this.bindingData.<T>getExplicitBinding((Key) Preconditions.checkNotNull(key, "key"));

        if (explicitBinding != null) {
            return explicitBinding;
        } else {
            synchronized (this.jitBindingData.lock()) {
                for (InjectorImpl injector = this; injector != null; injector = injector.parent) {
                    BindingImpl<T> jitBinding = injector.jitBindingData.getJitBinding(key);

                    if (jitBinding != null) {
                        return jitBinding;
                    }
                }
            }

            if (isProvider(key)) {
                try {
                    Key<?> providedKey = getProvidedKey(key, new Errors());

                    if (this.getExistingBinding(providedKey) != null) {
                        return this.getBinding(key);
                    }
                } catch (ErrorsException errorsexception) {
                    ConfigurationException exception = new ConfigurationException(errorsexception.getErrors().getMessages());

                    throw exception;
                }
            }

            return null;
        }
    }

    <T> BindingImpl<T> getBindingOrThrow(Key<T> key, Errors errors, InjectorImpl.JitLimitation jitType) throws ErrorsException {
        BindingImpl<T> binding = this.bindingData.<T>getExplicitBinding(key);

        return binding != null ? binding : this.getJustInTimeBinding(key, errors, jitType);
    }

    public <T> Binding<T> getBinding(Class<T> type) {
        return this.getBinding(Key.get((Class) Preconditions.checkNotNull(type, "type")));
    }

    public Injector getParent() {
        return this.parent;
    }

    public Injector createChildInjector(Iterable<? extends Module> modules) {
        return (new InternalInjectorCreator()).parentInjector(this).addModules(modules).build();
    }

    public Injector createChildInjector(Module... modules) {
        return this.createChildInjector(ImmutableList.copyOf(modules));
    }

    InjectorBindingData getBindingData() {
        return this.bindingData;
    }

    InjectorJitBindingData getJitBindingData() {
        return this.jitBindingData;
    }

    private <T> BindingImpl<T> getJustInTimeBinding(Key<T> key, Errors errors, InjectorImpl.JitLimitation jitType) throws ErrorsException {
        boolean jitOverride = isProvider(key) || isTypeLiteral(key) || isMembersInjector(key);

        synchronized (this.jitBindingData.lock()) {
            for (InjectorImpl injector = this; injector != null; injector = injector.parent) {
                BindingImpl<T> binding = injector.jitBindingData.getJitBinding(key);

                if (binding != null) {
                    if (this.options.jitDisabled && jitType == InjectorImpl.JitLimitation.NO_JIT && !jitOverride && !(binding instanceof InjectorImpl.ConvertedConstantBindingImpl)) {
                        throw errors.jitDisabled(key).toException();
                    }

                    return binding;
                }
            }

            if (this.jitBindingData.isFailedJitBinding(key) && errors.hasErrors()) {
                throw errors.toException();
            } else {
                return this.<T>createJustInTimeBindingRecursive(key, errors, this.options.jitDisabled, jitType);
            }
        }
    }

    private static boolean isProvider(Key<?> key) {
        return key.getTypeLiteral().getRawType().equals(Provider.class);
    }

    private static boolean isTypeLiteral(Key<?> key) {
        return key.getTypeLiteral().getRawType().equals(TypeLiteral.class);
    }

    private static <T> Key<T> getProvidedKey(Key<Provider<T>> key, Errors errors) throws ErrorsException {
        Type providerType = key.getTypeLiteral().getType();

        if (!(providerType instanceof ParameterizedType)) {
            throw errors.cannotInjectRawProvider().toException();
        } else {
            Type entryType = ((ParameterizedType) providerType).getActualTypeArguments()[0];
            Key<T> providedKey = key.ofType(entryType);

            return providedKey;
        }
    }

    private static boolean isMembersInjector(Key<?> key) {
        return key.getTypeLiteral().getRawType().equals(MembersInjector.class) && key.getAnnotationType() == null;
    }

    private <T> BindingImpl<MembersInjector<T>> createMembersInjectorBinding(Key<MembersInjector<T>> key, Errors errors) throws ErrorsException {
        Type membersInjectorType = key.getTypeLiteral().getType();

        if (!(membersInjectorType instanceof ParameterizedType)) {
            throw errors.cannotInjectRawMembersInjector().toException();
        } else {
            TypeLiteral<T> instanceType = TypeLiteral.get(((ParameterizedType) membersInjectorType).getActualTypeArguments()[0]);
            MembersInjector<T> membersInjector = this.membersInjectorStore.<T>get(instanceType, errors);
            InternalFactory<MembersInjector<T>> factory = new ConstantFactory<MembersInjector<T>>(Initializables.of(membersInjector));

            return new InstanceBindingImpl<MembersInjector<T>>(this, key, SourceProvider.UNKNOWN_SOURCE, factory, ImmutableSet.of(), membersInjector);
        }
    }

    private <T> BindingImpl<Provider<T>> createSyntheticProviderBinding(Key<Provider<T>> key, Errors errors) throws ErrorsException {
        Key<T> providedKey = getProvidedKey(key, errors);
        BindingImpl<T> delegate = this.<T>getBindingOrThrow(providedKey, errors, InjectorImpl.JitLimitation.NO_JIT);

        return new InjectorImpl.SyntheticProviderBindingImpl<Provider<T>>(this, key, delegate);
    }

    private <T> BindingImpl<T> convertConstantStringBinding(Key<T> key, Errors errors) throws ErrorsException {
        Key<String> stringKey = key.ofType(InjectorImpl.STRING_TYPE);
        BindingImpl<String> stringBinding = this.bindingData.<String>getExplicitBinding(stringKey);

        if (stringBinding != null && stringBinding.isConstant()) {
            String stringValue = (String) ((InstanceBinding) stringBinding).getInstance();
            Object source = stringBinding.getSource();
            TypeLiteral<T> type = key.getTypeLiteral();
            TypeConverterBinding typeConverterBinding = this.bindingData.getConverter(stringValue, type, errors, source);

            if (typeConverterBinding == null) {
                return null;
            } else {
                try {
                    T converted = (T) typeConverterBinding.getTypeConverter().convert(stringValue, type);

                    if (converted == null) {
                        throw errors.converterReturnedNull(stringValue, source, type, typeConverterBinding).toException();
                    } else if (!type.getRawType().isInstance(converted)) {
                        throw errors.conversionTypeError(stringValue, source, type, typeConverterBinding, converted).toException();
                    } else {
                        return new InjectorImpl.ConvertedConstantBindingImpl<T>(this, key, converted, stringBinding, typeConverterBinding);
                    }
                } catch (ErrorsException errorsexception) {
                    throw errorsexception;
                } catch (RuntimeException runtimeexception) {
                    throw errors.conversionError(stringValue, source, type, typeConverterBinding, runtimeexception).toException();
                }
            }
        } else {
            return null;
        }
    }

    <T> void initializeBinding(BindingImpl<T> binding, Errors errors) throws ErrorsException {
        if (binding instanceof DelayedInitialize) {
            ((DelayedInitialize) binding).initialize(this, errors);
        }

    }

    <T> void initializeJitBinding(BindingImpl<T> binding, Errors errors) throws ErrorsException {
        if (binding instanceof DelayedInitialize) {
            Key<T> key = binding.getKey();

            this.jitBindingData.putJitBinding(key, binding);
            boolean successful = false;
            DelayedInitialize delayed = (DelayedInitialize) binding;

            try {
                delayed.initialize(this, errors);
                successful = true;
            } finally {
                if (!successful) {
                    this.removeFailedJitBinding(binding, (InjectionPoint) null);
                    this.cleanup(binding, new HashSet());
                }

            }
        }

    }

    private boolean cleanup(BindingImpl<?> binding, Set<Key<?>> encountered) {
        boolean bindingFailed = false;

        for (Dependency<?> dep : this.getInternalDependencies(binding)) {
            Key<?> depKey = dep.getKey();
            InjectionPoint ip = dep.getInjectionPoint();

            if (encountered.add(depKey)) {
                BindingImpl<?> depBinding = this.jitBindingData.getJitBinding(depKey);

                if (depBinding != null) {
                    boolean failed = this.cleanup(depBinding, encountered);

                    if (depBinding instanceof ConstructorBindingImpl) {
                        ConstructorBindingImpl<?> ctorBinding = (ConstructorBindingImpl) depBinding;

                        ip = ctorBinding.getInternalConstructor();
                        if (!ctorBinding.isInitialized()) {
                            failed = true;
                        }
                    }

                    if (failed) {
                        this.removeFailedJitBinding(depBinding, ip);
                        bindingFailed = true;
                    }
                } else if (this.bindingData.getExplicitBinding(depKey) == null) {
                    bindingFailed = true;
                }
            }
        }

        return bindingFailed;
    }

    private void removeFailedJitBinding(Binding<?> binding, InjectionPoint ip) {
        this.jitBindingData.addFailedJitBinding(binding.getKey());
        if (ip == null || !this.constructors.isLoading(ip)) {
            this.jitBindingData.removeJitBinding(binding.getKey());
        }

        if (ip != null && !this.constructors.isLoading(ip)) {
            this.constructors.remove(ip);
        }

        this.membersInjectorStore.remove(binding.getKey().getTypeLiteral());
        this.provisionListenerStore.remove(binding);
    }

    private Set<Dependency<?>> getInternalDependencies(BindingImpl<?> binding) {
        return (Set<Dependency<?>>) (binding instanceof ConstructorBindingImpl ? ((ConstructorBindingImpl) binding).getInternalDependencies() : (binding instanceof HasDependencies ? ((HasDependencies) binding).getDependencies() : ImmutableSet.of()));
    }

    <T> BindingImpl<T> createUninitializedBinding(Key<T> key, Scoping scoping, Object source, Errors errors, boolean jitBinding, Consumer<CreationListener> creationListenerCallback) throws ErrorsException {
        Class<?> rawType = key.getTypeLiteral().getRawType();
        ImplementedBy implementedBy = (ImplementedBy) rawType.getAnnotation(ImplementedBy.class);

        if (!rawType.isArray() && (!rawType.isEnum() || implementedBy == null)) {
            if (rawType == TypeLiteral.class) {
                BindingImpl<T> binding = this.createTypeLiteralBinding(key, errors);

                return binding;
            } else if (implementedBy != null) {
                Annotations.checkForMisplacedScopeAnnotations(rawType, source, errors);
                return this.<T>createImplementedByBinding(key, scoping, implementedBy, errors, creationListenerCallback);
            } else {
                ProvidedBy providedBy = (ProvidedBy) rawType.getAnnotation(ProvidedBy.class);

                if (providedBy != null) {
                    Annotations.checkForMisplacedScopeAnnotations(rawType, source, errors);
                    return this.<T>createProvidedByBinding(key, scoping, providedBy, errors);
                } else {
                    return ConstructorBindingImpl.<T>create(this, key, (InjectionPoint) null, source, scoping, errors, jitBinding && this.options.jitDisabled, this.options.atInjectRequired);
                }
            }
        } else {
            throw errors.missingImplementationWithHint(key, this).toException();
        }
    }

    private <T> BindingImpl<TypeLiteral<T>> createTypeLiteralBinding(Key<TypeLiteral<T>> key, Errors errors) throws ErrorsException {
        Type typeLiteralType = key.getTypeLiteral().getType();

        if (!(typeLiteralType instanceof ParameterizedType)) {
            throw errors.cannotInjectRawTypeLiteral().toException();
        } else {
            ParameterizedType parameterizedType = (ParameterizedType) typeLiteralType;
            Type innerType = parameterizedType.getActualTypeArguments()[0];

            if (!(innerType instanceof Class) && !(innerType instanceof GenericArrayType) && !(innerType instanceof ParameterizedType)) {
                throw errors.cannotInjectTypeLiteralOf(innerType).toException();
            } else {
                TypeLiteral<T> value = TypeLiteral.get(innerType);
                InternalFactory<TypeLiteral<T>> factory = new ConstantFactory<TypeLiteral<T>>(Initializables.of(value));

                return new InstanceBindingImpl<TypeLiteral<T>>(this, key, SourceProvider.UNKNOWN_SOURCE, factory, ImmutableSet.of(), value);
            }
        }
    }

    <T> BindingImpl<T> createProvidedByBinding(Key<T> key, Scoping scoping, ProvidedBy providedBy, Errors errors) throws ErrorsException {
        Class<?> rawType = key.getTypeLiteral().getRawType();
        Class<? extends jakarta.inject.Provider<?>> providerType = providedBy.value();

        if (providerType == rawType) {
            throw errors.recursiveProviderType().toException();
        } else {
            if (!scoping.isExplicitlyScoped()) {
                int numErrorsBefore = errors.size();
                Class<? extends Annotation> scopeAnnotation = Annotations.findScopeAnnotation(errors, rawType);

                if (scopeAnnotation != null) {
                    scoping = Scoping.makeInjectable(Scoping.forAnnotation(scopeAnnotation), this, errors.withSource(rawType));
                }

                errors.throwIfNewErrors(numErrorsBefore);
            }

            Key<? extends Provider<T>> providerKey = Key.get(providerType);
            ProvidedByInternalFactory<T> internalFactory = new ProvidedByInternalFactory<T>(rawType, providerType, providerKey);
            BindingImpl<T> binding = LinkedProviderBindingImpl.<T>createWithInitializer(this, key, rawType, Scoping.scope(key, this, internalFactory, rawType, scoping), scoping, providerKey, internalFactory);

            internalFactory.setProvisionListenerCallback(this.provisionListenerStore.get(binding));
            return binding;
        }
    }

    private <T> BindingImpl<T> createImplementedByBinding(Key<T> key, Scoping scoping, ImplementedBy implementedBy, Errors errors, Consumer<CreationListener> creationListenerCallback) throws ErrorsException {
        Class<?> rawType = key.getTypeLiteral().getRawType();
        Class<?> implementationType = implementedBy.value();

        if (implementationType == rawType) {
            throw errors.recursiveImplementationType().toException();
        } else if (!rawType.isAssignableFrom(implementationType)) {
            throw errors.notASubtype(implementationType, rawType).toException();
        } else {
            Key<? extends T> targetKey = Key.get(implementationType);
            FactoryProxy<T> factory = new FactoryProxy<T>(this, key, targetKey, rawType);

            creationListenerCallback.accept(factory);
            return new LinkedBindingImpl<T>(this, key, rawType, Scoping.scope(key, this, factory, rawType, scoping), scoping, targetKey);
        }
    }

    private <T> BindingImpl<T> createJustInTimeBindingRecursive(Key<T> key, Errors errors, boolean jitDisabled, InjectorImpl.JitLimitation jitType) throws ErrorsException {
        if (this.parent != null) {
            if (jitType == InjectorImpl.JitLimitation.NEW_OR_EXISTING_JIT && jitDisabled && !this.parent.options.jitDisabled) {
                throw errors.jitDisabledInParent(key).toException();
            }

            try {
                return this.parent.<T>createJustInTimeBindingRecursive(key, new Errors(), jitDisabled, this.parent.options.jitDisabled ? InjectorImpl.JitLimitation.NO_JIT : jitType);
            } catch (ErrorsException errorsexception) {
                ;
            }
        }

        Set<Object> sources = this.jitBindingData.getSourcesForBannedKey(key);

        if (this.jitBindingData.isBannedKey(key)) {
            throw errors.childBindingAlreadySet(key, sources).toException();
        } else {
            key = MoreTypes.<T>canonicalizeKey(key);
            BindingImpl<T> binding = this.<T>createJustInTimeBinding(key, errors, jitDisabled, jitType);

            this.jitBindingData.banKeyInParent(key, this.bindingData, binding.getSource());
            this.jitBindingData.putJitBinding(key, binding);
            return binding;
        }
    }

    private <T> BindingImpl<T> createJustInTimeBinding(Key<T> key, Errors errors, boolean jitDisabled, InjectorImpl.JitLimitation jitType) throws ErrorsException {
        int numErrorsBefore = errors.size();
        Set<Object> sources = this.jitBindingData.getSourcesForBannedKey(key);

        if (this.jitBindingData.isBannedKey(key)) {
            throw errors.childBindingAlreadySet(key, sources).toException();
        } else if (isProvider(key)) {
            BindingImpl<T> binding = this.createSyntheticProviderBinding(key, errors);

            return binding;
        } else if (isMembersInjector(key)) {
            BindingImpl<T> binding = this.createMembersInjectorBinding(key, errors);

            return binding;
        } else {
            BindingImpl<T> convertedBinding = this.<T>convertConstantStringBinding(key, errors);

            if (convertedBinding != null) {
                return convertedBinding;
            } else if (!isTypeLiteral(key) && jitDisabled && jitType != InjectorImpl.JitLimitation.NEW_OR_EXISTING_JIT) {
                throw errors.jitDisabled(key).toException();
            } else if (key.getAnnotationType() == null) {
                Object source = key.getTypeLiteral().getRawType();
                BindingImpl<T> binding = this.<T>createUninitializedBinding(key, Scoping.UNSCOPED, source, errors, true, (creationListenerx) -> {
                    creationListenerx.notify(errors);
                });

                errors.throwIfNewErrors(numErrorsBefore);
                this.initializeJitBinding(binding, errors);
                return binding;
            } else {
                if (key.hasAttributes() && !this.options.exactBindingAnnotationsRequired) {
                    try {
                        Errors ignored = new Errors();

                        return this.<T>getBindingOrThrow(key.withoutAttributes(), ignored, InjectorImpl.JitLimitation.NO_JIT);
                    } catch (ErrorsException errorsexception) {
                        ;
                    }
                }

                throw errors.missingImplementationWithHint(key, this).toException();
            }
        }
    }

    <T> InternalFactory<? extends T> getInternalFactory(Key<T> key, Errors errors, InjectorImpl.JitLimitation jitType) throws ErrorsException {
        return this.<T>getBindingOrThrow(key, errors, jitType).getInternalFactory();
    }

    public Map<Key<?>, Binding<?>> getBindings() {
        return this.bindingData.getExplicitBindingsThisLevel();
    }

    public Map<Key<?>, Binding<?>> getAllBindings() {
        synchronized (this.jitBindingData.lock()) {
            return (new Builder()).putAll(this.bindingData.getExplicitBindingsThisLevel()).putAll(this.jitBindingData.getJitBindings()).buildOrThrow();
        }
    }

    public Map<Class<? extends Annotation>, Scope> getScopeBindings() {
        return ImmutableMap.copyOf(this.bindingData.getScopes());
    }

    public Set<TypeConverterBinding> getTypeConverterBindings() {
        return ImmutableSet.copyOf(this.bindingData.getConvertersThisLevel());
    }

    public List<com.google.inject.spi.Element> getElements() {
        ImmutableList.Builder<com.google.inject.spi.Element> elements = ImmutableList.builder();

        elements.addAll(this.getAllBindings().values());
        elements.addAll(this.bindingData.getProviderLookupsThisLevel());
        elements.addAll(this.bindingData.getConvertersThisLevel());
        elements.addAll(this.bindingData.getScopeBindingsThisLevel());
        elements.addAll(this.bindingData.getTypeListenerBindingsThisLevel());
        elements.addAll(this.bindingData.getProvisionListenerBindingsThisLevel());
        elements.addAll(this.bindingData.getScannerBindingsThisLevel());
        elements.addAll(this.bindingData.getStaticInjectionRequestsThisLevel());
        elements.addAll(this.bindingData.getMembersInjectorLookupsThisLevel());
        elements.addAll(this.bindingData.getInjectionRequestsThisLevel());
        elements.addAll(this.bindingData.getInterceptorBindingsThisLevel());
        return elements.build();
    }

    public Map<TypeLiteral<?>, List<InjectionPoint>> getAllMembersInjectorInjectionPoints() {
        ImmutableListMultimap immutablelistmultimap = this.membersInjectorStore.getAllInjectionPoints();
        Set set = this.userRequestedMembersInjectorTypes;

        Objects.requireNonNull(this.userRequestedMembersInjectorTypes);
        Map<TypeLiteral<?>, List<InjectionPoint>> res = ImmutableListMultimap.copyOf(Multimaps.filterKeys(immutablelistmultimap, set::contains)).asMap();

        return res;
    }

    SingleParameterInjector<?>[] getParametersInjectors(List<Dependency<?>> parameters, Errors errors) throws ErrorsException {
        if (parameters.isEmpty()) {
            return null;
        } else {
            int numErrorsBefore = errors.size();
            SingleParameterInjector<?>[] result = new SingleParameterInjector[parameters.size()];
            int i = 0;

            for (Dependency<?> parameter : parameters) {
                try {
                    result[i++] = this.createParameterInjector(parameter, errors.withSource(parameter));
                } catch (ErrorsException errorsexception) {
                    ;
                }
            }

            errors.throwIfNewErrors(numErrorsBefore);
            return result;
        }
    }

    <T> SingleParameterInjector<T> createParameterInjector(Dependency<T> dependency, Errors errors) throws ErrorsException {
        BindingImpl<? extends T> binding = this.<T>getBindingOrThrow(dependency.getKey(), errors, InjectorImpl.JitLimitation.NO_JIT);

        return new SingleParameterInjector<T>(dependency, binding);
    }

    public void injectMembers(Object instance) {
        MembersInjector membersInjector = this.getMembersInjector(instance.getClass());

        membersInjector.injectMembers(instance);
    }

    public <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> typeLiteral) {
        Preconditions.checkNotNull(typeLiteral, "typeLiteral");
        this.userRequestedMembersInjectorTypes.add(typeLiteral);
        Errors errors = new Errors(typeLiteral);

        try {
            return this.membersInjectorStore.<T>get(typeLiteral, errors);
        } catch (ErrorsException errorsexception) {
            ConfigurationException exception = new ConfigurationException(errors.merge(errorsexception.getErrors()).getMessages());

            throw exception;
        }
    }

    public <T> MembersInjector<T> getMembersInjector(Class<T> type) {
        return this.getMembersInjector(TypeLiteral.get(type));
    }

    public <T> Provider<T> getProvider(Class<T> type) {
        return this.getProvider(Key.get((Class) Preconditions.checkNotNull(type, "type")));
    }

    <T> Provider<T> getProviderOrThrow(final Dependency<T> dependency, Errors errors) throws ErrorsException {
        Key<T> key = dependency.getKey();
        BindingImpl<? extends T> binding = this.<T>getBindingOrThrow(key, errors, InjectorImpl.JitLimitation.NO_JIT);
        final InternalFactory<? extends T> internalFactory = binding.getInternalFactory();

        return new Provider<T>() {
            public T get() {
                InternalContext currentContext = InjectorImpl.this.enterContext();

                Object object;

                try {
                    T t = internalFactory.get(currentContext, dependency, false);

                    object = t;
                } catch (InternalProvisionException internalprovisionexception) {
                    throw internalprovisionexception.addSource(dependency).toProvisionException();
                } finally {
                    currentContext.close();
                }

                return (T) object;
            }

            public String toString() {
                return internalFactory.toString();
            }
        };
    }

    public <T> Provider<T> getProvider(Key<T> key) {
        Preconditions.checkNotNull(key, "key");
        Errors errors = new Errors(key);

        try {
            Provider<T> result = this.<T>getProviderOrThrow(Dependency.get(key), errors);

            errors.throwIfNewErrors(0);
            return result;
        } catch (ErrorsException errorsexception) {
            ConfigurationException exception = new ConfigurationException(errors.merge(errorsexception.getErrors()).getMessages());

            throw exception;
        }
    }

    public <T> T getInstance(Key<T> key) {
        return (T) this.getProvider(key).get();
    }

    public <T> T getInstance(Class<T> type) {
        return (T) this.getProvider(type).get();
    }

    InternalContext getLocalContext() {
        return (InternalContext) ((Object[]) this.localContext.get())[0];
    }

    InternalContext enterContext() {
        Object[] reference = this.localContext.get();

        if (reference == null) {
            reference = new Object[1];
            this.localContext.set(reference);
        }

        InternalContext ctx = (InternalContext) reference[0];

        if (ctx == null) {
            reference[0] = ctx = new InternalContext(this.options, reference);
        } else {
            ctx.enter();
        }

        return ctx;
    }

    public String toString() {
        return MoreObjects.toStringHelper(Injector.class).add("bindings", this.bindingData.getExplicitBindingsThisLevel().values()).toString();
    }

    static class InjectorOptions {

        final Stage stage;
        final boolean jitDisabled;
        final boolean disableCircularProxies;
        final boolean atInjectRequired;
        final boolean exactBindingAnnotationsRequired;

        InjectorOptions(Stage stage, boolean jitDisabled, boolean disableCircularProxies, boolean atInjectRequired, boolean exactBindingAnnotationsRequired) {
            super();
            this.stage = stage;
            this.jitDisabled = jitDisabled;
            this.disableCircularProxies = disableCircularProxies;
            this.atInjectRequired = atInjectRequired;
            this.exactBindingAnnotationsRequired = exactBindingAnnotationsRequired;
        }

        public String toString() {
            return MoreObjects.toStringHelper(this.getClass()).add("stage", this.stage).add("jitDisabled", this.jitDisabled).add("disableCircularProxies", this.disableCircularProxies).add("atInjectRequired", this.atInjectRequired).add("exactBindingAnnotationsRequired", this.exactBindingAnnotationsRequired).toString();
        }
    }

    static enum JitLimitation {

        NO_JIT, EXISTING_JIT, NEW_OR_EXISTING_JIT;

        private JitLimitation() {}
    }

    private static class SyntheticProviderBindingImpl<T> extends BindingImpl<Provider<T>> implements ProviderBinding<Provider<T>>, HasDependencies {

        final BindingImpl<T> providedBinding;

        SyntheticProviderBindingImpl(InjectorImpl injector, Key<Provider<T>> key, Binding<T> providedBinding) {
            super(injector, key, providedBinding.getSource(), createInternalFactory(providedBinding), Scoping.UNSCOPED);
            this.providedBinding = (BindingImpl) providedBinding;
        }

        static <T> InternalFactory<Provider<T>> createInternalFactory(Binding<T> providedBinding) {
            final Provider<T> provider = providedBinding.getProvider();

            return new InternalFactory<Provider<T>>() {
                public Provider<T> get(InternalContext context, Dependency<?> dependency, boolean linked) {
                    return provider;
                }
            };
        }

        public Key<? extends T> getProvidedKey() {
            return this.providedBinding.getKey();
        }

        public <V> V acceptTargetVisitor(BindingTargetVisitor<? super Provider<T>, V> visitor) {
            return (V) visitor.visit(this);
        }

        public void applyTo(Binder binder) {
            throw new UnsupportedOperationException("This element represents a synthetic binding.");
        }

        public String toString() {
            return MoreObjects.toStringHelper(ProviderBinding.class).add("key", this.getKey()).add("providedKey", this.getProvidedKey()).toString();
        }

        public Set<Dependency<?>> getDependencies() {
            return ImmutableSet.of(Dependency.get(this.getProvidedKey()));
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof InjectorImpl.SyntheticProviderBindingImpl)) {
                return false;
            } else {
                InjectorImpl.SyntheticProviderBindingImpl<?> o = (InjectorImpl.SyntheticProviderBindingImpl) obj;

                return this.getKey().equals(o.getKey()) && this.getScoping().equals(o.getScoping()) && com.google.common.base.Objects.equal(this.providedBinding, o.providedBinding);
            }
        }

        public int hashCode() {
            return com.google.common.base.Objects.hashCode(new Object[]{this.getKey(), this.getScoping(), this.providedBinding});
        }
    }

    private static class ConvertedConstantBindingImpl<T> extends BindingImpl<T> implements ConvertedConstantBinding<T> {

        final T value;
        final Provider<T> provider;
        final Binding<String> originalBinding;
        final TypeConverterBinding typeConverterBinding;

        ConvertedConstantBindingImpl(InjectorImpl injector, Key<T> key, T value, Binding<String> originalBinding, TypeConverterBinding typeConverterBinding) {
            super(injector, key, originalBinding.getSource(), new ConstantFactory(Initializables.of(value)), Scoping.UNSCOPED);
            this.value = value;
            this.provider = Providers.<T>of(value);
            this.originalBinding = originalBinding;
            this.typeConverterBinding = typeConverterBinding;
        }

        public Provider<T> getProvider() {
            return this.provider;
        }

        public <V> V acceptTargetVisitor(BindingTargetVisitor<? super T, V> visitor) {
            return (V) visitor.visit(this);
        }

        public T getValue() {
            return this.value;
        }

        public TypeConverterBinding getTypeConverterBinding() {
            return this.typeConverterBinding;
        }

        public Key<String> getSourceKey() {
            return this.originalBinding.getKey();
        }

        public Set<Dependency<?>> getDependencies() {
            return ImmutableSet.of(Dependency.get(this.getSourceKey()));
        }

        public void applyTo(Binder binder) {
            throw new UnsupportedOperationException("This element represents a synthetic binding.");
        }

        public String toString() {
            return MoreObjects.toStringHelper(ConvertedConstantBinding.class).add("key", this.getKey()).add("sourceKey", this.getSourceKey()).add("value", this.value).toString();
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof InjectorImpl.ConvertedConstantBindingImpl)) {
                return false;
            } else {
                InjectorImpl.ConvertedConstantBindingImpl<?> o = (InjectorImpl.ConvertedConstantBindingImpl) obj;

                return this.getKey().equals(o.getKey()) && this.getScoping().equals(o.getScoping()) && com.google.common.base.Objects.equal(this.value, o.value);
            }
        }

        public int hashCode() {
            return com.google.common.base.Objects.hashCode(new Object[]{this.getKey(), this.getScoping(), this.value});
        }
    }

    interface MethodInvoker {

        Object invoke(Object target, Object... parameters) throws IllegalAccessException, InvocationTargetException;
    }
}
