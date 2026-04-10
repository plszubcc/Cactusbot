package com.google.inject.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.MultibinderBinding;
import com.google.inject.multibindings.MultibindingsTargetVisitor;
import com.google.inject.name.Names;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.Message;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.ProviderWithExtensionVisitor;
import com.google.inject.util.Types;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public final class RealMultibinder<T> implements Module {

    private final RealMultibinder.BindingSelection<T> bindingSelection;
    private final Binder binder;

    public static <T> RealMultibinder<T> newRealSetBinder(Binder binder, Key<T> key) {
        RealMultibinder<T> result = new RealMultibinder<T>(binder, key);

        binder.install(result);
        return result;
    }

    static <T> TypeLiteral<Set<T>> setOf(TypeLiteral<T> elementType) {
        Type type = Types.setOf(elementType.getType());

        return TypeLiteral.get(type);
    }

    static <T> TypeLiteral<Collection<Provider<T>>> collectionOfProvidersOf(TypeLiteral<T> elementType) {
        Type providerType = Types.providerOf(elementType.getType());
        Type type = Types.collectionOf(providerType);

        return TypeLiteral.get(type);
    }

    static <T> TypeLiteral<Collection<jakarta.inject.Provider<T>>> collectionOfJakartaProvidersOf(TypeLiteral<T> elementType) {
        Type providerType = Types.newParameterizedType(jakarta.inject.Provider.class, elementType.getType());
        Type type = Types.collectionOf(providerType);

        return TypeLiteral.get(type);
    }

    static <T> TypeLiteral<Set<? extends T>> setOfExtendsOf(TypeLiteral<T> elementType) {
        Type extendsType = Types.subtypeOf(elementType.getType());
        Type setOfExtendsType = Types.setOf(extendsType);

        return TypeLiteral.get(setOfExtendsType);
    }

    RealMultibinder(Binder binder, Key<T> key) {
        super();
        this.binder = (Binder) Errors.checkNotNull(binder, "binder");
        this.bindingSelection = new RealMultibinder.BindingSelection<T>(key);
    }

    public void configure(Binder binder) {
        Errors.checkConfiguration(!this.bindingSelection.isInitialized(), "Multibinder was already initialized");
        binder.bind(this.bindingSelection.getSetKey()).toProvider(new RealMultibinder.RealMultibinderProvider(this.bindingSelection));
        binder.bind(this.bindingSelection.getSetOfExtendsKey()).to(this.bindingSelection.getSetKey());
        binder.bind(this.bindingSelection.getCollectionOfProvidersKey()).toProvider(new RealMultibinder.RealMultibinderCollectionOfProvidersProvider(this.bindingSelection));
        binder.bind(this.bindingSelection.getCollectionOfJakartaProvidersKey()).to(this.bindingSelection.getCollectionOfProvidersKey());
    }

    public void permitDuplicates() {
        this.binder.install(new RealMultibinder.PermitDuplicatesModule(this.bindingSelection.getPermitDuplicatesKey()));
    }

    Key<T> getKeyForNewItem() {
        Errors.checkConfiguration(!this.bindingSelection.isInitialized(), "Multibinder was already initialized");
        return Key.get(this.bindingSelection.getElementTypeLiteral(), (Annotation) (new RealElement(this.bindingSelection.getSetName(), Element.Type.MULTIBINDER, "")));
    }

    public LinkedBindingBuilder<T> addBinding() {
        return this.binder.bind(this.getKeyForNewItem());
    }

    Key<Set<T>> getSetKey() {
        return this.bindingSelection.getSetKey();
    }

    TypeLiteral<T> getElementTypeLiteral() {
        return this.bindingSelection.getElementTypeLiteral();
    }

    String getSetName() {
        return this.bindingSelection.getSetName();
    }

    boolean permitsDuplicates(Injector injector) {
        return this.bindingSelection.permitsDuplicates(injector);
    }

    boolean containsElement(com.google.inject.spi.Element element) {
        return this.bindingSelection.containsElement(element);
    }

    public boolean equals(Object o) {
        return o instanceof RealMultibinder && ((RealMultibinder) o).bindingSelection.equals(this.bindingSelection);
    }

    public int hashCode() {
        return this.bindingSelection.hashCode();
    }

    private abstract static class BaseFactory<ValueT, ProvidedT> extends InternalProviderInstanceBindingImpl.Factory<ProvidedT> {

        final Function<RealMultibinder.BindingSelection<ValueT>, ImmutableSet<Dependency<?>>> dependenciesFn;
        final RealMultibinder.BindingSelection<ValueT> bindingSelection;

        BaseFactory(RealMultibinder.BindingSelection<ValueT> bindingSelection, Function<RealMultibinder.BindingSelection<ValueT>, ImmutableSet<Dependency<?>>> dependenciesFn) {
            super(InternalProviderInstanceBindingImpl.InitializationTiming.DELAYED);
            this.bindingSelection = bindingSelection;
            this.dependenciesFn = dependenciesFn;
        }

        void initialize(InjectorImpl injector, Errors errors) throws ErrorsException {
            this.bindingSelection.initialize(injector, errors);
            this.doInitialize();
        }

        abstract void doInitialize();

        public Set<Dependency<?>> getDependencies() {
            return (Set) this.dependenciesFn.apply(this.bindingSelection);
        }

        public boolean equals(Object obj) {
            return this.getClass().isInstance(obj) && this.bindingSelection.equals(((RealMultibinder.BaseFactory) obj).bindingSelection);
        }

        public int hashCode() {
            return this.bindingSelection.hashCode();
        }
    }

    private static final class RealMultibinderProvider<T> extends RealMultibinder.BaseFactory<T, Set<T>> implements ProviderWithExtensionVisitor<Set<T>>, MultibinderBinding<Set<T>> {

        List<Binding<T>> bindings;
        SingleParameterInjector<T>[] injectors;
        boolean permitDuplicates;

        RealMultibinderProvider(RealMultibinder.BindingSelection<T> bindingSelection) {
            super(bindingSelection, (bsx) -> {
                return bsx.getDependencies();
            });
        }

        protected void doInitialize() {
            this.bindings = this.bindingSelection.getBindings();
            this.injectors = this.bindingSelection.getParameterInjectors();
            this.permitDuplicates = this.bindingSelection.permitsDuplicates();
        }

        protected ImmutableSet<T> doProvision(InternalContext context, Dependency<?> dependency) throws InternalProvisionException {
            SingleParameterInjector<T>[] localInjectors = this.injectors;

            if (localInjectors == null) {
                return ImmutableSet.of();
            } else {
                T[] values = (T[]) (new Object[localInjectors.length]);

                for (int i = 0; i < localInjectors.length; ++i) {
                    SingleParameterInjector<T> parameterInjector = localInjectors[i];
                    T newValue = parameterInjector.inject(context);

                    if (newValue == null) {
                        throw this.newNullEntryException(i);
                    }

                    values[i] = newValue;
                }

                ImmutableSet<T> set = ImmutableSet.copyOf(values);

                if (!this.permitDuplicates && set.size() < values.length) {
                    throw this.newDuplicateValuesException(values);
                } else {
                    return set;
                }
            }
        }

        private InternalProvisionException newNullEntryException(int i) {
            return InternalProvisionException.create(ErrorId.NULL_ELEMENT_IN_SET, "Set injection failed due to null element bound at: %s", ((Binding) this.bindings.get(i)).getSource());
        }

        private InternalProvisionException newDuplicateValuesException(T[] values) {
            Message message = new Message(GuiceInternal.GUICE_INTERNAL, ErrorId.DUPLICATE_ELEMENT, new DuplicateElementError(this.bindingSelection.getSetKey(), this.bindings, values, ImmutableList.of(this.getSource())));

            return new InternalProvisionException(message);
        }

        public <B, V> V acceptExtensionVisitor(BindingTargetVisitor<B, V> visitor, ProviderInstanceBinding<? extends B> binding) {
            return (V) (visitor instanceof MultibindingsTargetVisitor ? ((MultibindingsTargetVisitor) visitor).visit(this) : visitor.visit(binding));
        }

        public Key<Set<T>> getSetKey() {
            return this.bindingSelection.getSetKey();
        }

        public ImmutableSet<Key<?>> getAlternateSetKeys() {
            return ImmutableSet.of(this.bindingSelection.getCollectionOfProvidersKey(), this.bindingSelection.getCollectionOfJakartaProvidersKey(), this.bindingSelection.getSetOfExtendsKey());
        }

        public TypeLiteral<?> getElementTypeLiteral() {
            return this.bindingSelection.getElementTypeLiteral();
        }

        public List<Binding<?>> getElements() {
            return this.bindingSelection.getElements();
        }

        public boolean permitsDuplicates() {
            return this.bindingSelection.permitsDuplicates();
        }

        public boolean containsElement(com.google.inject.spi.Element element) {
            return this.bindingSelection.containsElement(element);
        }
    }

    private static final class RealMultibinderCollectionOfProvidersProvider<T> extends RealMultibinder.BaseFactory<T, Collection<Provider<T>>> {

        ImmutableList<Provider<T>> providers;

        RealMultibinderCollectionOfProvidersProvider(RealMultibinder.BindingSelection<T> bindingSelection) {
            super(bindingSelection, (bsx) -> {
                return bsx.getProviderDependencies();
            });
        }

        protected void doInitialize() {
            ImmutableList.Builder<Provider<T>> providers = ImmutableList.builder();
            UnmodifiableIterator unmodifiableiterator = this.bindingSelection.getBindings().iterator();

            while (unmodifiableiterator.hasNext()) {
                Binding<T> binding = (Binding) unmodifiableiterator.next();

                providers.add(binding.getProvider());
            }

            this.providers = providers.build();
        }

        protected ImmutableList<Provider<T>> doProvision(InternalContext context, Dependency<?> dependency) {
            return this.providers;
        }
    }

    private static final class BindingSelection<T> {

        private static final ImmutableSet<Dependency<?>> MODULE_DEPENDENCIES = ImmutableSet.of(Dependency.get(Key.get(Injector.class)));
        private final TypeLiteral<T> elementType;
        private final Key<Set<T>> setKey;
        private String setName;
        private Key<Collection<Provider<T>>> collectionOfProvidersKey;
        private Key<Collection<jakarta.inject.Provider<T>>> collectionOfJakartaProvidersKey;
        private Key<Set<? extends T>> setOfExtendsKey;
        private Key<Boolean> permitDuplicatesKey;
        private boolean isInitialized;
        private ImmutableList<Binding<T>> bindings;
        private ImmutableSet<Dependency<?>> dependencies;
        private ImmutableSet<Dependency<?>> providerDependencies;
        private boolean permitDuplicates;
        private SingleParameterInjector<T>[] parameterinjectors;

        BindingSelection(Key<T> key) {
            super();
            this.dependencies = RealMultibinder.BindingSelection.MODULE_DEPENDENCIES;
            this.providerDependencies = RealMultibinder.BindingSelection.MODULE_DEPENDENCIES;
            this.setKey = key.ofType(RealMultibinder.setOf(key.getTypeLiteral()));
            this.elementType = key.getTypeLiteral();
        }

        void initialize(InjectorImpl injector, Errors errors) throws ErrorsException {
            if (!this.isInitialized) {
                List<Binding<T>> bindings = Lists.newArrayList();
                Set<Indexer.IndexedBinding> index = Sets.newHashSet();
                Indexer indexer = new Indexer(injector);
                List<Dependency<?>> dependencies = Lists.newArrayList();
                List<Dependency<?>> providerDependencies = Lists.newArrayList();

                for (Binding<?> entry : injector.findBindingsByType(this.elementType)) {
                    if (this.keyMatches(entry.getKey()) && index.add((Indexer.IndexedBinding) entry.acceptTargetVisitor(indexer))) {
                        bindings.add(entry);
                        Key<T> key = entry.getKey();

                        dependencies.add(Dependency.get(key));
                        providerDependencies.add(Dependency.get(key.ofType(Types.providerOf(key.getTypeLiteral().getType()))));
                    }
                }

                this.bindings = ImmutableList.copyOf(bindings);
                this.dependencies = ImmutableSet.copyOf(dependencies);
                this.providerDependencies = ImmutableSet.copyOf(providerDependencies);
                this.permitDuplicates = this.permitsDuplicates(injector);
                SingleParameterInjector<T>[] typed = injector.getParametersInjectors(dependencies, errors);

                this.parameterinjectors = typed;
                this.isInitialized = true;
            }
        }

        boolean permitsDuplicates(Injector injector) {
            return injector.getBindings().containsKey(this.getPermitDuplicatesKey());
        }

        ImmutableList<Binding<T>> getBindings() {
            Errors.checkConfiguration(this.isInitialized, "not initialized");
            return this.bindings;
        }

        SingleParameterInjector<T>[] getParameterInjectors() {
            Errors.checkConfiguration(this.isInitialized, "not initialized");
            return this.parameterinjectors;
        }

        ImmutableSet<Dependency<?>> getDependencies() {
            return this.dependencies;
        }

        ImmutableSet<Dependency<?>> getProviderDependencies() {
            return this.providerDependencies;
        }

        String getSetName() {
            if (this.setName == null) {
                this.setName = Annotations.nameOf(this.setKey);
            }

            return this.setName;
        }

        Key<Boolean> getPermitDuplicatesKey() {
            Key<Boolean> local = this.permitDuplicatesKey;

            if (local == null) {
                local = this.permitDuplicatesKey = Key.get(Boolean.class, (Annotation) Names.named(this.toString() + " permits duplicates"));
            }

            return local;
        }

        Key<Collection<Provider<T>>> getCollectionOfProvidersKey() {
            Key<Collection<Provider<T>>> local = this.collectionOfProvidersKey;

            if (local == null) {
                local = this.collectionOfProvidersKey = this.setKey.ofType(RealMultibinder.collectionOfProvidersOf(this.elementType));
            }

            return local;
        }

        Key<Collection<jakarta.inject.Provider<T>>> getCollectionOfJakartaProvidersKey() {
            Key<Collection<jakarta.inject.Provider<T>>> local = this.collectionOfJakartaProvidersKey;

            if (local == null) {
                local = this.collectionOfJakartaProvidersKey = this.setKey.ofType(RealMultibinder.collectionOfJakartaProvidersOf(this.elementType));
            }

            return local;
        }

        Key<Set<? extends T>> getSetOfExtendsKey() {
            Key<Set<? extends T>> local = this.setOfExtendsKey;

            if (local == null) {
                local = this.setOfExtendsKey = this.setKey.ofType(RealMultibinder.setOfExtendsOf(this.elementType));
            }

            return local;
        }

        boolean isInitialized() {
            return this.isInitialized;
        }

        TypeLiteral<T> getElementTypeLiteral() {
            return this.elementType;
        }

        Key<Set<T>> getSetKey() {
            return this.setKey;
        }

        List<Binding<?>> getElements() {
            if (this.isInitialized()) {
                return this.bindings;
            } else {
                throw new UnsupportedOperationException("getElements() not supported for module bindings");
            }
        }

        boolean permitsDuplicates() {
            if (this.isInitialized()) {
                return this.permitDuplicates;
            } else {
                throw new UnsupportedOperationException("permitsDuplicates() not supported for module bindings");
            }
        }

        boolean containsElement(com.google.inject.spi.Element element) {
            if (!(element instanceof Binding)) {
                return false;
            } else {
                Binding<?> binding = (Binding) element;

                return this.keyMatches(binding.getKey()) || binding.getKey().equals(this.getPermitDuplicatesKey()) || binding.getKey().equals(this.setKey) || binding.getKey().equals(this.collectionOfProvidersKey) || binding.getKey().equals(this.collectionOfJakartaProvidersKey) || binding.getKey().equals(this.setOfExtendsKey);
            }
        }

        private boolean keyMatches(Key<?> key) {
            return key.getTypeLiteral().equals(this.elementType) && key.getAnnotation() instanceof Element && ((Element) key.getAnnotation()).setName().equals(this.getSetName()) && ((Element) key.getAnnotation()).type() == Element.Type.MULTIBINDER;
        }

        public boolean equals(Object obj) {
            return obj instanceof RealMultibinder.BindingSelection ? this.setKey.equals(((RealMultibinder.BindingSelection) obj).setKey) : false;
        }

        public int hashCode() {
            return this.setKey.hashCode();
        }

        public String toString() {
            return (this.getSetName().isEmpty() ? "" : this.getSetName() + " ") + "Multibinder<" + this.elementType + ">";
        }
    }

    private static class PermitDuplicatesModule extends AbstractModule {

        private final Key<Boolean> key;

        PermitDuplicatesModule(Key<Boolean> key) {
            super();
            this.key = key;
        }

        protected void configure() {
            this.bind(this.key).toInstance(true);
        }

        public boolean equals(Object o) {
            return o instanceof RealMultibinder.PermitDuplicatesModule && ((RealMultibinder.PermitDuplicatesModule) o).key.equals(this.key);
        }

        public int hashCode() {
            return this.getClass().hashCode() ^ this.key.hashCode();
        }
    }
}
