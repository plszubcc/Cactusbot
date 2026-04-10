package com.google.inject.internal;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.MapBinderBinding;
import com.google.inject.multibindings.MultibindingsTargetVisitor;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.ProviderWithExtensionVisitor;
import com.google.inject.util.Types;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class RealMapBinder<K, V> implements Module {

    private static final ImmutableSet<Dependency<?>> MODULE_DEPENDENCIES = ImmutableSet.of(Dependency.get(Key.get(Injector.class)));
    private final RealMapBinder.BindingSelection<K, V> bindingSelection;
    private final Binder binder;
    private final RealMultibinder<Map.Entry<K, Provider<V>>> entrySetBinder;

    public static <K, V> RealMapBinder<K, V> newMapRealBinder(Binder binder, TypeLiteral<K> keyType, TypeLiteral<V> valueType) {
        return newRealMapBinder(binder, keyType, valueType, Key.get(mapOf(keyType, valueType)), RealMultibinder.newRealSetBinder(binder, Key.get(entryOfProviderOf(keyType, valueType))));
    }

    public static <K, V> RealMapBinder<K, V> newRealMapBinder(Binder binder, TypeLiteral<K> keyType, TypeLiteral<V> valueType, Annotation annotation) {
        return newRealMapBinder(binder, keyType, valueType, Key.get(mapOf(keyType, valueType), annotation), RealMultibinder.newRealSetBinder(binder, Key.get(entryOfProviderOf(keyType, valueType), annotation)));
    }

    public static <K, V> RealMapBinder<K, V> newRealMapBinder(Binder binder, TypeLiteral<K> keyType, TypeLiteral<V> valueType, Class<? extends Annotation> annotationType) {
        return newRealMapBinder(binder, keyType, valueType, Key.get(mapOf(keyType, valueType), annotationType), RealMultibinder.newRealSetBinder(binder, Key.get(entryOfProviderOf(keyType, valueType), annotationType)));
    }

    static <K, V> TypeLiteral<Map<K, V>> mapOf(TypeLiteral<K> keyType, TypeLiteral<V> valueType) {
        return TypeLiteral.get(Types.mapOf(keyType.getType(), valueType.getType()));
    }

    static <K, V> TypeLiteral<Map<K, Provider<V>>> mapOfProviderOf(TypeLiteral<K> keyType, TypeLiteral<V> valueType) {
        return TypeLiteral.get(Types.mapOf(keyType.getType(), Types.providerOf(valueType.getType())));
    }

    static <K, V> TypeLiteral<Map<K, jakarta.inject.Provider<V>>> mapOfJakartaProviderOf(TypeLiteral<K> keyType, TypeLiteral<V> valueType) {
        return TypeLiteral.get(Types.mapOf(keyType.getType(), Types.newParameterizedType(jakarta.inject.Provider.class, valueType.getType())));
    }

    static <K, V> TypeLiteral<Map<K, Set<Provider<V>>>> mapOfSetOfProviderOf(TypeLiteral<K> keyType, TypeLiteral<V> valueType) {
        return TypeLiteral.get(Types.mapOf(keyType.getType(), Types.setOf(Types.providerOf(valueType.getType()))));
    }

    static <K, V> TypeLiteral<Map<K, Set<jakarta.inject.Provider<V>>>> mapOfSetOfJakartaProviderOf(TypeLiteral<K> keyType, TypeLiteral<V> valueType) {
        return TypeLiteral.get(Types.mapOf(keyType.getType(), Types.setOf(Types.jakartaProviderOf(valueType.getType()))));
    }

    static <K, V> TypeLiteral<Map<K, Collection<Provider<V>>>> mapOfCollectionOfProviderOf(TypeLiteral<K> keyType, TypeLiteral<V> valueType) {
        return TypeLiteral.get(Types.mapOf(keyType.getType(), Types.collectionOf(Types.providerOf(valueType.getType()))));
    }

    static <K, V> TypeLiteral<Map<K, Collection<jakarta.inject.Provider<V>>>> mapOfCollectionOfJakartaProviderOf(TypeLiteral<K> keyType, TypeLiteral<V> valueType) {
        return TypeLiteral.get(Types.mapOf(keyType.getType(), Types.collectionOf(Types.jakartaProviderOf(valueType.getType()))));
    }

    static <K, V> TypeLiteral<Map.Entry<K, Provider<V>>> entryOfProviderOf(TypeLiteral<K> keyType, TypeLiteral<V> valueType) {
        return TypeLiteral.get(Types.newParameterizedTypeWithOwner(Map.class, Entry.class, keyType.getType(), Types.providerOf(valueType.getType())));
    }

    static <K, V> TypeLiteral<Map.Entry<K, Provider<V>>> entryOfJakartaProviderOf(TypeLiteral<K> keyType, TypeLiteral<V> valueType) {
        return TypeLiteral.get(Types.newParameterizedTypeWithOwner(Map.class, Entry.class, keyType.getType(), Types.jakartaProviderOf(valueType.getType())));
    }

    static <K, V> TypeLiteral<Set<Map.Entry<K, jakarta.inject.Provider<V>>>> setOfEntryOfJakartaProviderOf(TypeLiteral<K> keyType, TypeLiteral<V> valueType) {
        return TypeLiteral.get(Types.setOf(entryOfJakartaProviderOf(keyType, valueType).getType()));
    }

    private static <T> Key<Provider<T>> getKeyOfProvider(Key<T> valueKey) {
        return valueKey.ofType(Types.providerOf(valueKey.getTypeLiteral().getType()));
    }

    static <K, V> RealMapBinder<K, V> newRealMapBinder(Binder binder, TypeLiteral<K> keyType, Key<V> valueTypeAndAnnotation) {
        TypeLiteral<V> valueType = valueTypeAndAnnotation.getTypeLiteral();

        return newRealMapBinder(binder, keyType, valueType, valueTypeAndAnnotation.ofType(mapOf(keyType, valueType)), RealMultibinder.newRealSetBinder(binder, valueTypeAndAnnotation.ofType(entryOfProviderOf(keyType, valueType))));
    }

    private static <K, V> RealMapBinder<K, V> newRealMapBinder(Binder binder, TypeLiteral<K> keyType, TypeLiteral<V> valueType, Key<Map<K, V>> mapKey, RealMultibinder<Map.Entry<K, Provider<V>>> entrySetBinder) {
        RealMapBinder<K, V> mapBinder = new RealMapBinder<K, V>(binder, keyType, valueType, mapKey, entrySetBinder);

        binder.install(mapBinder);
        return mapBinder;
    }

    private RealMapBinder(Binder binder, TypeLiteral<K> keyType, TypeLiteral<V> valueType, Key<Map<K, V>> mapKey, RealMultibinder<Map.Entry<K, Provider<V>>> entrySetBinder) {
        super();
        this.bindingSelection = new RealMapBinder.BindingSelection<K, V>(keyType, valueType, mapKey, entrySetBinder);
        this.binder = binder;
        this.entrySetBinder = entrySetBinder;
    }

    public void permitDuplicates() {
        Errors.checkConfiguration(!this.bindingSelection.isInitialized(), "MapBinder was already initialized");
        this.entrySetBinder.permitDuplicates();
        this.binder.install(new RealMapBinder.MultimapBinder(this.bindingSelection));
    }

    Key<V> getKeyForNewValue(K key) {
        Errors.checkNotNull(key, "key");
        Errors.checkConfiguration(!this.bindingSelection.isInitialized(), "MapBinder was already initialized");
        RealMultibinder<Map.Entry<K, Provider<V>>> entrySetBinder = this.bindingSelection.getEntrySetBinder();
        Key<V> valueKey = Key.get(this.bindingSelection.getValueType(), (Annotation) (new RealElement(entrySetBinder.getSetName(), Element.Type.MAPBINDER, this.bindingSelection.getKeyType().toString())));

        entrySetBinder.addBinding().toProvider(new RealMapBinder.ProviderMapEntry(key, valueKey));
        return valueKey;
    }

    public LinkedBindingBuilder<V> addBinding(K key) {
        return this.binder.bind(this.getKeyForNewValue(key));
    }

    public void configure(Binder binder) {
        Errors.checkConfiguration(!this.bindingSelection.isInitialized(), "MapBinder was already initialized");
        binder.bind(this.bindingSelection.getProviderMapKey()).toProvider(new RealMapBinder.RealProviderMapProvider(this.bindingSelection));
        binder.bind(this.bindingSelection.getJakartaProviderMapKey()).to(this.bindingSelection.getProviderMapKey());
        binder.bind(this.bindingSelection.getMapKey()).toProvider(new RealMapBinder.ExtensionRealMapProvider(this.bindingSelection));
        binder.bind(this.bindingSelection.getMapOfKeyExtendsValueKey()).to(this.bindingSelection.getMapKey());
        binder.bind(this.bindingSelection.getEntrySetJakartaProviderKey()).to(this.bindingSelection.getEntrySetBinder().getSetKey());
    }

    public boolean equals(Object o) {
        return o instanceof RealMapBinder && ((RealMapBinder) o).bindingSelection.equals(this.bindingSelection);
    }

    public int hashCode() {
        return this.bindingSelection.hashCode();
    }

    private static <K, V> InternalProvisionException createNullValueException(K key, Binding<V> binding) {
        return InternalProvisionException.create(ErrorId.NULL_VALUE_IN_MAP, "Map injection failed due to null value for key \"%s\", bound at: %s", key, binding.getSource());
    }

    private static final class BindingSelection<K, V> {

        private final TypeLiteral<K> keyType;
        private final TypeLiteral<V> valueType;
        private final Key<Map<K, V>> mapKey;
        private Key<Map<K, Provider<V>>> providerMapKey;
        private Key<Map<K, Set<V>>> multimapKey;
        private Key<Map<K, Set<Provider<V>>>> providerSetMultimapKey;
        private Key<Map<K, Collection<Provider<V>>>> providerCollectionMultimapKey;
        private Key<Map<K, ? extends V>> mapOfKeyExtendsValueKey;
        private Key<Map<K, jakarta.inject.Provider<V>>> jakartaProviderMapKey;
        private Key<Map<K, Set<jakarta.inject.Provider<V>>>> jakartaProviderSetMultimapKey;
        private Key<Map<K, Collection<jakarta.inject.Provider<V>>>> jakartaProviderCollectionMultimapKey;
        private Key<Set<Map.Entry<K, jakarta.inject.Provider<V>>>> entrySetJakartaProviderKey;
        private final RealMultibinder<Map.Entry<K, Provider<V>>> entrySetBinder;
        private RealMapBinder.BindingSelection.InitializationState initializationState;
        private ImmutableMap<K, Binding<V>> mapBindings;
        private ImmutableMap<K, Set<Binding<V>>> multimapBindings;
        private ImmutableList<Map.Entry<K, Binding<V>>> entries;
        private boolean permitsDuplicates;

        private BindingSelection(TypeLiteral<K> keyType, TypeLiteral<V> valueType, Key<Map<K, V>> mapKey, RealMultibinder<Map.Entry<K, Provider<V>>> entrySetBinder) {
            super();
            this.keyType = keyType;
            this.valueType = valueType;
            this.mapKey = mapKey;
            this.entrySetBinder = entrySetBinder;
            this.initializationState = RealMapBinder.BindingSelection.InitializationState.UNINITIALIZED;
        }

        private boolean tryInitialize(InjectorImpl injector, Errors errors) {
            if (this.initializationState != RealMapBinder.BindingSelection.InitializationState.UNINITIALIZED) {
                return this.initializationState != RealMapBinder.BindingSelection.InitializationState.HAS_ERRORS;
            } else {
                this.permitsDuplicates = this.entrySetBinder.permitsDuplicates(injector);
                Map<K, ImmutableSet.Builder<Binding<V>>> bindingMultimapMutable = new LinkedHashMap();
                Map<K, Binding<V>> bindingMapMutable = new LinkedHashMap();
                Multimap<K, Indexer.IndexedBinding> index = HashMultimap.create();
                Indexer indexer = new Indexer(injector);
                Multimap<K, Binding<V>> duplicates = null;
                ImmutableList.Builder<Map.Entry<K, Binding<V>>> entriesBuilder = ImmutableList.builder();

                for (Binding<Map.Entry<K, Provider<V>>> binding : injector.findBindingsByType(this.entrySetBinder.getElementTypeLiteral())) {
                    if (this.entrySetBinder.containsElement(binding)) {
                        ProviderInstanceBinding<RealMapBinder.ProviderMapEntry<K, V>> entryBinding = (ProviderInstanceBinding) binding;
                        RealMapBinder.ProviderMapEntry<K, V> entry = (RealMapBinder.ProviderMapEntry) entryBinding.getUserSuppliedProvider();
                        K key = entry.getKey();
                        Key<V> valueKey = entry.getValueKey();
                        Binding<V> valueBinding = injector.getExistingBinding(valueKey);

                        if (index.put(key, (Indexer.IndexedBinding) valueBinding.acceptTargetVisitor(indexer))) {
                            entriesBuilder.add(Maps.immutableEntry(key, valueBinding));
                            Binding<V> previous = (Binding) bindingMapMutable.put(key, valueBinding);

                            if (previous != null && !this.permitsDuplicates) {
                                if (duplicates == null) {
                                    duplicates = LinkedHashMultimap.create();
                                }

                                duplicates.put(key, previous);
                                duplicates.put(key, valueBinding);
                            }

                            if (this.permitsDuplicates) {
                                ((Builder) bindingMultimapMutable.computeIfAbsent(key, (kx) -> {
                                    return ImmutableSet.builder();
                                })).add(valueBinding);
                            }
                        }
                    }
                }

                if (duplicates != null) {
                    this.initializationState = RealMapBinder.BindingSelection.InitializationState.HAS_ERRORS;
                    reportDuplicateKeysError(this.mapKey, duplicates, errors);
                    return false;
                } else {
                    ImmutableMap.Builder<K, Set<Binding<V>>> bindingsMultimapBuilder = ImmutableMap.builder();

                    for (Map.Entry<K, ImmutableSet.Builder<Binding<V>>> entry : bindingMultimapMutable.entrySet()) {
                        bindingsMultimapBuilder.put(entry.getKey(), ((Builder) entry.getValue()).build());
                    }

                    this.mapBindings = ImmutableMap.copyOf(bindingMapMutable);
                    this.multimapBindings = bindingsMultimapBuilder.buildOrThrow();
                    this.entries = entriesBuilder.build();
                    this.initializationState = RealMapBinder.BindingSelection.InitializationState.INITIALIZED;
                    return true;
                }
            }
        }

        private static <K, V> void reportDuplicateKeysError(Key<Map<K, V>> mapKey, Multimap<K, Binding<V>> duplicates, Errors errors) {
            errors.duplicateMapKey(mapKey, duplicates);
        }

        private boolean containsElement(com.google.inject.spi.Element element) {
            if (this.entrySetBinder.containsElement(element)) {
                return true;
            } else if (!(element instanceof Binding)) {
                return false;
            } else {
                Key<?> key = ((Binding) element).getKey();

                return key.equals(this.getMapKey()) || key.equals(this.getProviderMapKey()) || key.equals(this.getJakartaProviderMapKey()) || key.equals(this.getMultimapKey()) || key.equals(this.getProviderSetMultimapKey()) || key.equals(this.getJakartaProviderSetMultimapKey()) || key.equals(this.getProviderCollectionMultimapKey()) || key.equals(this.getJakartaProviderCollectionMultimapKey()) || key.equals(this.entrySetBinder.getSetKey()) || key.equals(this.getEntrySetJakartaProviderKey()) || key.equals(this.getMapOfKeyExtendsValueKey()) || this.matchesValueKey(key);
            }
        }

        private boolean matchesValueKey(Key<?> key) {
            return key.getAnnotation() instanceof RealElement && ((RealElement) key.getAnnotation()).setName().equals(this.entrySetBinder.getSetName()) && ((RealElement) key.getAnnotation()).type() == Element.Type.MAPBINDER && ((RealElement) key.getAnnotation()).keyType().equals(this.keyType.toString()) && key.getTypeLiteral().equals(this.valueType);
        }

        private Key<Map<K, Provider<V>>> getProviderMapKey() {
            Key<Map<K, Provider<V>>> local = this.providerMapKey;

            if (local == null) {
                local = this.providerMapKey = this.mapKey.ofType(RealMapBinder.mapOfProviderOf(this.keyType, this.valueType));
            }

            return local;
        }

        private Key<Map<K, jakarta.inject.Provider<V>>> getJakartaProviderMapKey() {
            Key<Map<K, jakarta.inject.Provider<V>>> local = this.jakartaProviderMapKey;

            if (local == null) {
                local = this.jakartaProviderMapKey = this.mapKey.ofType(RealMapBinder.mapOfJakartaProviderOf(this.keyType, this.valueType));
            }

            return local;
        }

        private Key<Map<K, Set<V>>> getMultimapKey() {
            Key<Map<K, Set<V>>> local = this.multimapKey;

            if (local == null) {
                local = this.multimapKey = this.mapKey.ofType(RealMapBinder.mapOf(this.keyType, RealMultibinder.setOf(this.valueType)));
            }

            return local;
        }

        private Key<Map<K, Set<Provider<V>>>> getProviderSetMultimapKey() {
            Key<Map<K, Set<Provider<V>>>> local = this.providerSetMultimapKey;

            if (local == null) {
                local = this.providerSetMultimapKey = this.mapKey.ofType(RealMapBinder.mapOfSetOfProviderOf(this.keyType, this.valueType));
            }

            return local;
        }

        private Key<Map<K, Set<jakarta.inject.Provider<V>>>> getJakartaProviderSetMultimapKey() {
            Key<Map<K, Set<jakarta.inject.Provider<V>>>> local = this.jakartaProviderSetMultimapKey;

            if (local == null) {
                local = this.jakartaProviderSetMultimapKey = this.mapKey.ofType(RealMapBinder.mapOfSetOfJakartaProviderOf(this.keyType, this.valueType));
            }

            return local;
        }

        private Key<Map<K, Collection<Provider<V>>>> getProviderCollectionMultimapKey() {
            Key<Map<K, Collection<Provider<V>>>> local = this.providerCollectionMultimapKey;

            if (local == null) {
                local = this.providerCollectionMultimapKey = this.mapKey.ofType(RealMapBinder.mapOfCollectionOfProviderOf(this.keyType, this.valueType));
            }

            return local;
        }

        private Key<Map<K, Collection<jakarta.inject.Provider<V>>>> getJakartaProviderCollectionMultimapKey() {
            Key<Map<K, Collection<jakarta.inject.Provider<V>>>> local = this.jakartaProviderCollectionMultimapKey;

            if (local == null) {
                local = this.jakartaProviderCollectionMultimapKey = this.mapKey.ofType(RealMapBinder.mapOfCollectionOfJakartaProviderOf(this.keyType, this.valueType));
            }

            return local;
        }

        private Key<Set<Map.Entry<K, jakarta.inject.Provider<V>>>> getEntrySetJakartaProviderKey() {
            Key<Set<Map.Entry<K, jakarta.inject.Provider<V>>>> local = this.entrySetJakartaProviderKey;

            if (local == null) {
                local = this.entrySetJakartaProviderKey = this.mapKey.ofType(RealMapBinder.setOfEntryOfJakartaProviderOf(this.keyType, this.valueType));
            }

            return local;
        }

        private Key<Map<K, ? extends V>> getMapOfKeyExtendsValueKey() {
            Key<Map<K, ? extends V>> local = this.mapOfKeyExtendsValueKey;

            if (local == null) {
                Type extendsValue = Types.subtypeOf(this.valueType.getType());
                Type mapOfKeyAndExtendsValue = Types.mapOf(this.keyType.getType(), extendsValue);

                local = this.mapOfKeyExtendsValueKey = this.mapKey.ofType(mapOfKeyAndExtendsValue);
            }

            return local;
        }

        private ImmutableMap<K, Binding<V>> getMapBindings() {
            Errors.checkConfiguration(this.isInitialized(), "MapBinder has not yet been initialized");
            return this.mapBindings;
        }

        private ImmutableMap<K, Set<Binding<V>>> getMultimapBindings() {
            Errors.checkConfiguration(this.isInitialized(), "MapBinder has not yet been initialized");
            return this.multimapBindings;
        }

        private ImmutableList<Map.Entry<K, Binding<V>>> getEntries() {
            Errors.checkConfiguration(this.isInitialized(), "MapBinder has not yet been initialized");
            return this.entries;
        }

        private boolean isInitialized() {
            return this.initializationState == RealMapBinder.BindingSelection.InitializationState.INITIALIZED;
        }

        private TypeLiteral<K> getKeyType() {
            return this.keyType;
        }

        private TypeLiteral<V> getValueType() {
            return this.valueType;
        }

        private Key<Map<K, V>> getMapKey() {
            return this.mapKey;
        }

        private RealMultibinder<Map.Entry<K, Provider<V>>> getEntrySetBinder() {
            return this.entrySetBinder;
        }

        private boolean permitsDuplicates() {
            if (this.isInitialized()) {
                return this.permitsDuplicates;
            } else {
                throw new UnsupportedOperationException("permitsDuplicates() not supported for module bindings");
            }
        }

        public boolean equals(Object o) {
            return o instanceof RealMapBinder.BindingSelection && ((RealMapBinder.BindingSelection) o).mapKey.equals(this.mapKey);
        }

        public int hashCode() {
            return this.mapKey.hashCode();
        }

        private static enum InitializationState {

            UNINITIALIZED, INITIALIZED, HAS_ERRORS;

            private InitializationState() {}
        }
    }

    private static final class RealProviderMapProvider<K, V> extends RealMapBinder.RealMapBinderProviderWithDependencies<K, V, Map<K, Provider<V>>> {

        private Map<K, Provider<V>> mapOfProviders;
        private Set<Dependency<?>> dependencies;
        private boolean initialized;

        private RealProviderMapProvider(RealMapBinder.BindingSelection<K, V> bindingSelection) {
            super(bindingSelection, null);
            this.dependencies = RealMapBinder.MODULE_DEPENDENCIES;
        }

        public Set<Dependency<?>> getDependencies() {
            return this.dependencies;
        }

        protected void doInitialize(InjectorImpl injector, Errors errors) {
            if (!this.initialized) {
                this.initialized = true;
                ImmutableMap.Builder<K, Provider<V>> mapOfProvidersBuilder = ImmutableMap.builder();
                ImmutableSet.Builder<Dependency<?>> dependenciesBuilder = ImmutableSet.builder();
                UnmodifiableIterator unmodifiableiterator = this.bindingSelection.getMapBindings().entrySet().iterator();

                while (unmodifiableiterator.hasNext()) {
                    Map.Entry<K, Binding<V>> entry = (Entry) unmodifiableiterator.next();

                    mapOfProvidersBuilder.put(entry.getKey(), ((Binding) entry.getValue()).getProvider());
                    dependenciesBuilder.add(Dependency.get(RealMapBinder.getKeyOfProvider(((Binding) entry.getValue()).getKey())));
                }

                this.mapOfProviders = mapOfProvidersBuilder.buildOrThrow();
                this.dependencies = dependenciesBuilder.build();
            }
        }

        protected Map<K, Provider<V>> doProvision(InternalContext context, Dependency<?> dependency) {
            return this.mapOfProviders;
        }
    }

    private static final class ExtensionRealMapProvider<K, V> extends RealMapBinder.RealMapBinderProviderWithDependencies<K, V, Map<K, V>> implements ProviderWithExtensionVisitor<Map<K, V>>, MapBinderBinding<Map<K, V>> {

        Set<Dependency<?>> dependencies;
        SingleParameterInjector<V>[] injectors;
        K[] keys;
        private boolean initialized;

        ExtensionRealMapProvider(RealMapBinder.BindingSelection<K, V> bindingSelection) {
            super(bindingSelection, null);
            this.dependencies = RealMapBinder.MODULE_DEPENDENCIES;
            this.initialized = false;
        }

        protected void doInitialize(InjectorImpl injector, Errors errors) throws ErrorsException {
            if (!this.initialized) {
                this.initialized = true;
                K[] keysArray = (K[]) (new Object[this.bindingSelection.getMapBindings().size()]);

                this.keys = keysArray;
                ImmutableSet.Builder<Dependency<?>> dependenciesBuilder = ImmutableSet.builder();
                int i = 0;

                for (UnmodifiableIterator unmodifiableiterator = this.bindingSelection.getMapBindings().entrySet().iterator(); unmodifiableiterator.hasNext(); ++i) {
                    Map.Entry<K, Binding<V>> entry = (Entry) unmodifiableiterator.next();

                    dependenciesBuilder.add(Dependency.get(((Binding) entry.getValue()).getKey()));
                    this.keys[i] = entry.getKey();
                }

                ImmutableSet<Dependency<?>> localDependencies = dependenciesBuilder.build();

                this.dependencies = localDependencies;
                List<Dependency<?>> dependenciesList = localDependencies.asList();
                SingleParameterInjector<V>[] typedInjectors = injector.getParametersInjectors(dependenciesList, errors);

                this.injectors = typedInjectors;
            }
        }

        protected Map<K, V> doProvision(InternalContext context, Dependency<?> dependency) throws InternalProvisionException {
            SingleParameterInjector<V>[] localInjectors = this.injectors;

            if (localInjectors == null) {
                return ImmutableMap.of();
            } else {
                ImmutableMap.Builder<K, V> resultBuilder = ImmutableMap.builder();
                K[] localKeys = this.keys;

                for (int i = 0; i < localInjectors.length; ++i) {
                    SingleParameterInjector<V> injector = localInjectors[i];
                    K key = (K) localKeys[i];
                    V value = injector.inject(context);

                    if (value == null) {
                        throw RealMapBinder.createNullValueException(key, (Binding) this.bindingSelection.getMapBindings().get(key));
                    }

                    resultBuilder.put(key, value);
                }

                return resultBuilder.buildOrThrow();
            }
        }

        public Set<Dependency<?>> getDependencies() {
            return this.dependencies;
        }

        RealMapBinder.BindingSelection<K, V> getBindingSelection() {
            return this.bindingSelection;
        }

        public <B, W> W acceptExtensionVisitor(BindingTargetVisitor<B, W> visitor, ProviderInstanceBinding<? extends B> binding) {
            return (W) (visitor instanceof MultibindingsTargetVisitor ? ((MultibindingsTargetVisitor) visitor).visit(this) : visitor.visit(binding));
        }

        public Key<Map<K, V>> getMapKey() {
            return this.bindingSelection.getMapKey();
        }

        public Set<Key<?>> getAlternateMapKeys() {
            return ImmutableSet.of(this.bindingSelection.getJakartaProviderMapKey(), this.bindingSelection.getProviderMapKey(), this.bindingSelection.getProviderSetMultimapKey(), this.bindingSelection.getJakartaProviderSetMultimapKey(), this.bindingSelection.getProviderCollectionMultimapKey(), this.bindingSelection.getJakartaProviderCollectionMultimapKey(), new Key[]{this.bindingSelection.getMultimapKey(), this.bindingSelection.getMapOfKeyExtendsValueKey()});
        }

        public TypeLiteral<K> getKeyTypeLiteral() {
            return this.bindingSelection.getKeyType();
        }

        public TypeLiteral<V> getValueTypeLiteral() {
            return this.bindingSelection.getValueType();
        }

        public List<Map.Entry<?, Binding<?>>> getEntries() {
            if (this.bindingSelection.isInitialized()) {
                return this.bindingSelection.getEntries();
            } else {
                throw new UnsupportedOperationException("getEntries() not supported for module bindings");
            }
        }

        public List<Map.Entry<?, Binding<?>>> getEntries(Iterable<? extends com.google.inject.spi.Element> elements) {
            ImmutableMultimap.Builder<K, Key<V>> keyToValueKeyBuilder = ImmutableMultimap.builder();
            ImmutableMap.Builder<Key<V>, Binding<V>> valueKeyToBindingBuilder = ImmutableMap.builder();
            ImmutableMap.Builder<Key<V>, K> valueKeyToKeyBuilder = ImmutableMap.builder();
            ImmutableMap.Builder<Key<V>, Binding<Map.Entry<K, Provider<V>>>> valueKeyToEntryBindingBuilder = ImmutableMap.builder();

            for (com.google.inject.spi.Element element : elements) {
                if (element instanceof Binding) {
                    Binding<?> binding = (Binding) element;

                    if (this.bindingSelection.matchesValueKey(binding.getKey()) && binding.getKey().getTypeLiteral().equals(this.bindingSelection.valueType)) {
                        Key<V> typedKey = binding.getKey();

                        valueKeyToBindingBuilder.put(typedKey, binding);
                    }
                }

                if (element instanceof ProviderInstanceBinding && this.bindingSelection.getEntrySetBinder().containsElement(element)) {
                    ProviderInstanceBinding<Map.Entry<K, Provider<V>>> entryBinding = (ProviderInstanceBinding) element;
                    Provider<Map.Entry<K, Provider<V>>> typedProvider = (Provider) entryBinding.getUserSuppliedProvider();

                    if (typedProvider instanceof RealMapBinder.ProviderMapEntry) {
                        RealMapBinder.ProviderMapEntry<K, V> typedUserSuppliedProvider = (RealMapBinder.ProviderMapEntry) typedProvider;

                        keyToValueKeyBuilder.put(typedUserSuppliedProvider.getKey(), typedUserSuppliedProvider.getValueKey());
                        valueKeyToEntryBindingBuilder.put(typedUserSuppliedProvider.getValueKey(), entryBinding);
                        valueKeyToKeyBuilder.put(typedUserSuppliedProvider.getValueKey(), typedUserSuppliedProvider.getKey());
                    }
                }
            }

            ImmutableMultimap<K, Key<V>> keyToValueKey = keyToValueKeyBuilder.build();
            ImmutableMap<Key<V>, K> valueKeyToKey = valueKeyToKeyBuilder.buildOrThrow();
            ImmutableMap<Key<V>, Binding<V>> valueKeyToBinding = valueKeyToBindingBuilder.buildOrThrow();
            ImmutableMap<Key<V>, Binding<Map.Entry<K, Provider<V>>>> valueKeyToEntryBinding = valueKeyToEntryBindingBuilder.buildOrThrow();
            Set<Key<V>> keysFromProviderMapEntrys = Sets.newHashSet(keyToValueKey.values());
            Set<Key<V>> keysFromBindings = valueKeyToBinding.keySet();

            if (!keysFromProviderMapEntrys.equals(keysFromBindings)) {
                Set<Key<V>> keysOnlyFromProviderMapEntrys = Sets.difference(keysFromProviderMapEntrys, keysFromBindings);
                Set<Key<V>> keysOnlyFromBindings = Sets.difference(keysFromBindings, keysFromProviderMapEntrys);
                StringBuilder sb = new StringBuilder("Expected a 1:1 mapping from map keys to values.");

                if (!keysOnlyFromBindings.isEmpty()) {
                    sb.append(Errors.format("\nFound these Bindings that were missing an associated entry:\n"));

                    for (Key<V> key : keysOnlyFromBindings) {
                        sb.append(Errors.format("  %s bound at: %s\n", key, ((Binding) valueKeyToBinding.get(key)).getSource()));
                    }
                }

                if (!keysOnlyFromProviderMapEntrys.isEmpty()) {
                    sb.append(Errors.format("\nFound these map keys without a corresponding value:\n"));

                    for (Key<V> key : keysOnlyFromProviderMapEntrys) {
                        sb.append(Errors.format("  '%s' bound at: %s\n", valueKeyToKey.get(key), ((Binding) valueKeyToEntryBinding.get(key)).getSource()));
                    }
                }

                throw new IllegalArgumentException(sb.toString());
            } else {
                ImmutableList.Builder<Map.Entry<?, Binding<?>>> resultBuilder = ImmutableList.builder();
                UnmodifiableIterator keysOnlyFromBindings = keyToValueKey.entries().iterator();

                while (keysOnlyFromBindings.hasNext()) {
                    Map.Entry<K, Key<V>> entry = (Entry) keysOnlyFromBindings.next();
                    Binding<?> binding = (Binding) valueKeyToBinding.get(entry.getValue());
                    Map.Entry<?, Binding<?>> newEntry = Maps.immutableEntry(entry.getKey(), binding);

                    resultBuilder.add(newEntry);
                }

                return resultBuilder.build();
            }
        }

        public boolean permitsDuplicates() {
            if (this.bindingSelection.isInitialized()) {
                return this.bindingSelection.permitsDuplicates();
            } else {
                throw new UnsupportedOperationException("permitsDuplicates() not supported for module bindings");
            }
        }

        public boolean containsElement(com.google.inject.spi.Element element) {
            return this.bindingSelection.containsElement(element);
        }
    }

    static final class MultimapBinder<K, V> implements Module {

        private final RealMapBinder.BindingSelection<K, V> bindingSelection;

        private MultimapBinder(RealMapBinder.BindingSelection<K, V> bindingSelection) {
            super();
            this.bindingSelection = bindingSelection;
        }

        public void configure(Binder binder) {
            binder.bind(this.bindingSelection.getProviderSetMultimapKey()).toProvider(new RealMapBinder.MultimapBinder.RealProviderMultimapProvider(this.bindingSelection.getMapKey()));
            binder.bind(this.bindingSelection.getJakartaProviderSetMultimapKey()).to(this.bindingSelection.getProviderSetMultimapKey());
            binder.bind(this.bindingSelection.getProviderCollectionMultimapKey()).to(this.bindingSelection.getProviderSetMultimapKey());
            binder.bind(this.bindingSelection.getJakartaProviderCollectionMultimapKey()).to(this.bindingSelection.getProviderSetMultimapKey());
            binder.bind(this.bindingSelection.getMultimapKey()).toProvider(new RealMapBinder.MultimapBinder.RealMultimapProvider(this.bindingSelection.getMapKey()));
        }

        public int hashCode() {
            return this.bindingSelection.hashCode();
        }

        public boolean equals(Object o) {
            return o instanceof RealMapBinder.MultimapBinder && ((RealMapBinder.MultimapBinder) o).bindingSelection.equals(this.bindingSelection);
        }

        private static final class RealProviderMultimapProvider<K, V> extends RealMapBinder.RealMultimapBinderProviderWithDependencies<K, V, Map<K, Set<Provider<V>>>> {

            private Map<K, Set<Provider<V>>> multimapOfProviders;
            private Set<Dependency<?>> dependencies;
            private boolean initialized;

            private RealProviderMultimapProvider(Key<Map<K, V>> mapKey) {
                super(mapKey, null);
                this.dependencies = RealMapBinder.MODULE_DEPENDENCIES;
            }

            public Set<Dependency<?>> getDependencies() {
                return this.dependencies;
            }

            protected void doInitialize(InjectorImpl injector, Errors errors) {
                if (!this.initialized) {
                    this.initialized = true;
                    ImmutableMap.Builder<K, Set<Provider<V>>> multimapOfProvidersBuilder = ImmutableMap.builder();
                    ImmutableSet.Builder<Dependency<?>> dependenciesBuilder = ImmutableSet.builder();
                    UnmodifiableIterator unmodifiableiterator = this.bindingSelection.getMultimapBindings().entrySet().iterator();

                    while (unmodifiableiterator.hasNext()) {
                        Map.Entry<K, Set<Binding<V>>> entry = (Entry) unmodifiableiterator.next();
                        ImmutableSet.Builder<Provider<V>> providersBuilder = ImmutableSet.builder();

                        for (Binding<V> binding : (Set) entry.getValue()) {
                            providersBuilder.add(binding.getProvider());
                            dependenciesBuilder.add(Dependency.get(RealMapBinder.getKeyOfProvider(binding.getKey())));
                        }

                        multimapOfProvidersBuilder.put(entry.getKey(), providersBuilder.build());
                    }

                    this.multimapOfProviders = multimapOfProvidersBuilder.buildOrThrow();
                    this.dependencies = dependenciesBuilder.build();
                }
            }

            protected Map<K, Set<Provider<V>>> doProvision(InternalContext context, Dependency<?> dependency) {
                return this.multimapOfProviders;
            }
        }

        private static final class RealMultimapProvider<K, V> extends RealMapBinder.RealMultimapBinderProviderWithDependencies<K, V, Map<K, Set<V>>> {

            private Set<Dependency<?>> dependencies;
            private RealMapBinder.MultimapBinder.RealMultimapProvider.PerKeyData<K, V>[] perKeyDatas;
            private boolean initialized;

            private RealMultimapProvider(Key<Map<K, V>> mapKey) {
                super(mapKey, null);
                this.dependencies = RealMapBinder.MODULE_DEPENDENCIES;
                this.initialized = false;
            }

            public Set<Dependency<?>> getDependencies() {
                return this.dependencies;
            }

            protected void doInitialize(InjectorImpl injector, Errors errors) throws ErrorsException {
                if (!this.initialized) {
                    this.initialized = true;
                    RealMapBinder.MultimapBinder.RealMultimapProvider.PerKeyData<K, V>[] typedPerKeyData = new RealMapBinder.MultimapBinder.RealMultimapProvider.PerKeyData[this.bindingSelection.getMapBindings().size()];

                    this.perKeyDatas = typedPerKeyData;
                    ImmutableSet.Builder<Dependency<?>> dependenciesBuilder = ImmutableSet.builder();
                    List<Dependency<?>> dependenciesForKey = Lists.newArrayList();
                    int i = 0;

                    for (UnmodifiableIterator unmodifiableiterator = this.bindingSelection.getMultimapBindings().entrySet().iterator(); unmodifiableiterator.hasNext(); ++i) {
                        Map.Entry<K, Set<Binding<V>>> entry = (Entry) unmodifiableiterator.next();

                        dependenciesForKey.clear();
                        Set<Binding<V>> bindings = (Set) entry.getValue();
                        Binding<V>[] typedBindings = new Binding[bindings.size()];
                        Binding<V>[] bindingsArray = typedBindings;
                        int j = 0;

                        for (Binding<V> binding : bindings) {
                            Dependency<V> dependency = Dependency.<V>get(binding.getKey());

                            dependenciesBuilder.add(dependency);
                            dependenciesForKey.add(dependency);
                            bindingsArray[j] = binding;
                            ++j;
                        }

                        SingleParameterInjector<V>[] injectors = injector.getParametersInjectors(dependenciesForKey, errors);

                        this.perKeyDatas[i] = new RealMapBinder.MultimapBinder.RealMultimapProvider.PerKeyData(entry.getKey(), bindingsArray, injectors);
                    }

                    this.dependencies = dependenciesBuilder.build();
                }
            }

            protected Map<K, Set<V>> doProvision(InternalContext context, Dependency<?> dependency) throws InternalProvisionException {
                ImmutableMap.Builder<K, Set<V>> resultBuilder = ImmutableMap.builder();

                for (RealMapBinder.MultimapBinder.RealMultimapProvider.PerKeyData<K, V> perKeyData : this.perKeyDatas) {
                    ImmutableSet.Builder<V> bindingsBuilder = ImmutableSet.builder();
                    SingleParameterInjector<V>[] injectors = perKeyData.injectors;

                    for (int i = 0; i < injectors.length; ++i) {
                        SingleParameterInjector<V> injector = injectors[i];
                        V value = injector.inject(context);

                        if (value == null) {
                            throw RealMapBinder.createNullValueException(perKeyData.key, perKeyData.bindings[i]);
                        }

                        bindingsBuilder.add(value);
                    }

                    resultBuilder.put(perKeyData.key, bindingsBuilder.build());
                }

                return resultBuilder.buildOrThrow();
            }

            private static final class PerKeyData<K, V> {

                private final K key;
                private final Binding<V>[] bindings;
                private final SingleParameterInjector<V>[] injectors;

                private PerKeyData(K key, Binding<V>[] bindings, SingleParameterInjector<V>[] injectors) {
                    super();
                    Preconditions.checkArgument(bindings.length == injectors.length);
                    this.key = key;
                    this.bindings = bindings;
                    this.injectors = injectors;
                }
            }
        }
    }

    static final class ProviderMapEntry<K, V> extends InternalProviderInstanceBindingImpl.Factory<Map.Entry<K, Provider<V>>> {

        private final K key;
        private final Key<V> valueKey;
        private Map.Entry<K, Provider<V>> entry;

        ProviderMapEntry(K key, Key<V> valueKey) {
            super(InternalProviderInstanceBindingImpl.InitializationTiming.EAGER);
            this.key = key;
            this.valueKey = valueKey;
        }

        public Set<Dependency<?>> getDependencies() {
            return ImmutableSet.of(Dependency.get(RealMapBinder.getKeyOfProvider(this.valueKey)));
        }

        void initialize(InjectorImpl injector, Errors errors) {
            Binding<V> valueBinding = injector.getExistingBinding(this.valueKey);

            this.entry = Maps.immutableEntry(this.key, valueBinding.getProvider());
        }

        protected Map.Entry<K, Provider<V>> doProvision(InternalContext context, Dependency<?> dependency) {
            return this.entry;
        }

        K getKey() {
            return this.key;
        }

        Key<V> getValueKey() {
            return this.valueKey;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof RealMapBinder.ProviderMapEntry)) {
                return false;
            } else {
                RealMapBinder.ProviderMapEntry<?, ?> o = (RealMapBinder.ProviderMapEntry) obj;

                return this.key.equals(o.key) && this.valueKey.equals(o.valueKey);
            }
        }

        public int hashCode() {
            return Objects.hashCode(new Object[]{this.key, this.valueKey});
        }

        public String toString() {
            return "ProviderMapEntry(" + this.key + ", " + this.valueKey + ")";
        }
    }

    private abstract static class RealMapBinderProviderWithDependencies<K, V, P> extends InternalProviderInstanceBindingImpl.Factory<P> {

        final RealMapBinder.BindingSelection<K, V> bindingSelection;

        private RealMapBinderProviderWithDependencies(RealMapBinder.BindingSelection<K, V> bindingSelection) {
            super(InternalProviderInstanceBindingImpl.InitializationTiming.DELAYED);
            this.bindingSelection = bindingSelection;
        }

        final void initialize(InjectorImpl injector, Errors errors) throws ErrorsException {
            if (this.bindingSelection.tryInitialize(injector, errors)) {
                this.doInitialize(injector, errors);
            }

        }

        protected abstract void doInitialize(InjectorImpl injector, Errors errors) throws ErrorsException;

        public boolean equals(Object obj) {
            return obj != null && this.getClass() == obj.getClass() && this.bindingSelection.equals(((RealMapBinder.RealMapBinderProviderWithDependencies) obj).bindingSelection);
        }

        public int hashCode() {
            return this.bindingSelection.hashCode();
        }
    }

    private abstract static class RealMultimapBinderProviderWithDependencies<K, V, P> extends InternalProviderInstanceBindingImpl.Factory<P> {

        final Key<Map<K, V>> mapKey;
        RealMapBinder.BindingSelection<K, V> bindingSelection;

        private RealMultimapBinderProviderWithDependencies(Key<Map<K, V>> mapKey) {
            super(InternalProviderInstanceBindingImpl.InitializationTiming.DELAYED);
            this.mapKey = mapKey;
        }

        final void initialize(InjectorImpl injector, Errors errors) throws ErrorsException {
            Binding<Map<K, V>> mapBinding = injector.getExistingBinding(this.mapKey);
            ProviderInstanceBinding<Map<K, V>> providerInstanceBinding = (ProviderInstanceBinding) mapBinding;
            RealMapBinder.ExtensionRealMapProvider<K, V> mapProvider = (RealMapBinder.ExtensionRealMapProvider) providerInstanceBinding.getUserSuppliedProvider();

            this.bindingSelection = mapProvider.getBindingSelection();
            if (this.bindingSelection.tryInitialize(injector, errors)) {
                this.doInitialize(injector, errors);
            }

        }

        abstract void doInitialize(InjectorImpl injector, Errors errors) throws ErrorsException;

        public boolean equals(Object obj) {
            return obj != null && this.getClass() == obj.getClass() && this.mapKey.equals(((RealMapBinder.RealMultimapBinderProviderWithDependencies) obj).mapKey);
        }

        public int hashCode() {
            return this.mapKey.hashCode();
        }
    }
}
