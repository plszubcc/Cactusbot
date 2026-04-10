package com.google.inject.internal;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.MultibindingsTargetVisitor;
import com.google.inject.multibindings.OptionalBinderBinding;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.ProviderWithExtensionVisitor;
import com.google.inject.util.Types;
import jakarta.inject.Qualifier;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Type;
import java.util.Set;

public final class RealOptionalBinder<T> implements Module {

    private final RealOptionalBinder.BindingSelection<T> bindingSelection;
    private final Binder binder;

    public static <T> RealOptionalBinder<T> newRealOptionalBinder(Binder binder, Key<T> type) {
        binder = binder.skipSources(RealOptionalBinder.class);
        RealOptionalBinder<T> optionalBinder = new RealOptionalBinder<T>(binder, type);

        binder.install(optionalBinder);
        return optionalBinder;
    }

    static <T> TypeLiteral<Optional<T>> optionalOf(TypeLiteral<T> type) {
        return TypeLiteral.get(Types.newParameterizedType(Optional.class, type.getType()));
    }

    static <T> TypeLiteral<java.util.Optional<T>> javaOptionalOf(TypeLiteral<T> type) {
        return TypeLiteral.get(Types.newParameterizedType(java.util.Optional.class, type.getType()));
    }

    static <T> TypeLiteral<Optional<jakarta.inject.Provider<T>>> optionalOfJakartaProvider(TypeLiteral<T> type) {
        return TypeLiteral.get(Types.newParameterizedType(Optional.class, Types.newParameterizedType(jakarta.inject.Provider.class, type.getType())));
    }

    static <T> TypeLiteral<java.util.Optional<jakarta.inject.Provider<T>>> javaOptionalOfJakartaProvider(TypeLiteral<T> type) {
        return TypeLiteral.get(Types.newParameterizedType(java.util.Optional.class, Types.newParameterizedType(jakarta.inject.Provider.class, type.getType())));
    }

    static <T> TypeLiteral<Optional<Provider<T>>> optionalOfProvider(TypeLiteral<T> type) {
        return TypeLiteral.get(Types.newParameterizedType(Optional.class, Types.newParameterizedType(Provider.class, type.getType())));
    }

    static <T> TypeLiteral<java.util.Optional<Provider<T>>> javaOptionalOfProvider(TypeLiteral<T> type) {
        return TypeLiteral.get(Types.newParameterizedType(java.util.Optional.class, Types.newParameterizedType(Provider.class, type.getType())));
    }

    static <T> Key<Provider<T>> providerOf(Key<T> key) {
        Type providerT = Types.providerOf(key.getTypeLiteral().getType());

        return key.ofType(providerT);
    }

    private RealOptionalBinder(Binder binder, Key<T> typeKey) {
        super();
        this.bindingSelection = new RealOptionalBinder.BindingSelection<T>(typeKey);
        this.binder = binder;
    }

    private void addDirectTypeBinding(Binder binder) {
        binder.bind(this.bindingSelection.getDirectKey()).toProvider(new RealOptionalBinder.RealDirectTypeProvider(this.bindingSelection));
    }

    Key<T> getKeyForDefaultBinding() {
        this.bindingSelection.checkNotInitialized();
        this.addDirectTypeBinding(this.binder);
        return this.bindingSelection.getKeyForDefaultBinding();
    }

    public LinkedBindingBuilder<T> setDefault() {
        return this.binder.bind(this.getKeyForDefaultBinding());
    }

    Key<T> getKeyForActualBinding() {
        this.bindingSelection.checkNotInitialized();
        this.addDirectTypeBinding(this.binder);
        return this.bindingSelection.getKeyForActualBinding();
    }

    public LinkedBindingBuilder<T> setBinding() {
        return this.binder.bind(this.getKeyForActualBinding());
    }

    public void configure(Binder binder) {
        this.bindingSelection.checkNotInitialized();
        Key<T> key = this.bindingSelection.getDirectKey();
        TypeLiteral<T> typeLiteral = key.getTypeLiteral();
        Key<Optional<Provider<T>>> guavaOptProviderKey = key.ofType(optionalOfProvider(typeLiteral));

        binder.bind(guavaOptProviderKey).toProvider(new RealOptionalBinder.RealOptionalProviderProvider(this.bindingSelection));
        Key<java.util.Optional<Provider<T>>> javaOptProviderKey = key.ofType(javaOptionalOfProvider(typeLiteral));

        binder.bind(javaOptProviderKey).toProvider(new RealOptionalBinder.JavaOptionalProviderProvider(this.bindingSelection));
        binder.bind(key.ofType(optionalOfJakartaProvider(typeLiteral))).to(guavaOptProviderKey);
        binder.bind(key.ofType(javaOptionalOfJakartaProvider(typeLiteral))).to(javaOptProviderKey);
        Key<Optional<T>> guavaOptKey = key.ofType(optionalOf(typeLiteral));

        binder.bind(guavaOptKey).toProvider(new RealOptionalBinder.RealOptionalKeyProvider(this.bindingSelection, guavaOptKey));
        Key<java.util.Optional<T>> javaOptKey = key.ofType(javaOptionalOf(typeLiteral));

        binder.bind(javaOptKey).toProvider(new RealOptionalBinder.JavaOptionalProvider(this.bindingSelection, javaOptKey));
    }

    public boolean equals(Object o) {
        return o instanceof RealOptionalBinder && ((RealOptionalBinder) o).bindingSelection.equals(this.bindingSelection);
    }

    public int hashCode() {
        return this.bindingSelection.hashCode();
    }

    static enum Source {

        DEFAULT, ACTUAL;

        private Source() {}
    }

    private static final class JavaOptionalProvider<T> extends RealOptionalBinder.RealOptionalBinderProviderWithDependencies<T, java.util.Optional<T>> implements ProviderWithExtensionVisitor<java.util.Optional<T>>, OptionalBinderBinding<java.util.Optional<T>> {

        private final Key<java.util.Optional<T>> optionalKey;
        private Dependency<?> targetDependency;
        private InternalFactory<? extends T> target;

        JavaOptionalProvider(RealOptionalBinder.BindingSelection<T> bindingSelection, Key<java.util.Optional<T>> optionalKey) {
            super(bindingSelection);
            this.optionalKey = optionalKey;
        }

        void doInitialize() {
            if (this.bindingSelection.getBinding() != null) {
                this.target = this.bindingSelection.getBinding().getInternalFactory();
                this.targetDependency = this.bindingSelection.getDependency();
            }

        }

        protected java.util.Optional<T> doProvision(InternalContext context, Dependency<?> currentDependency) throws InternalProvisionException {
            InternalFactory<? extends T> local = this.target;

            if (local == null) {
                return java.util.Optional.empty();
            } else {
                Dependency<?> localDependency = this.targetDependency;

                T result;

                try {
                    result = local.get(context, localDependency, false);
                } catch (InternalProvisionException internalprovisionexception) {
                    throw internalprovisionexception.addSource(localDependency);
                }

                return java.util.Optional.ofNullable(result);
            }
        }

        public Set<Dependency<?>> getDependencies() {
            return this.bindingSelection.dependencies;
        }

        public <B, R> R acceptExtensionVisitor(BindingTargetVisitor<B, R> visitor, ProviderInstanceBinding<? extends B> binding) {
            return (R) (visitor instanceof MultibindingsTargetVisitor ? ((MultibindingsTargetVisitor) visitor).visit(this) : visitor.visit(binding));
        }

        public boolean containsElement(com.google.inject.spi.Element element) {
            return this.bindingSelection.containsElement(element);
        }

        public Binding<?> getActualBinding() {
            return this.bindingSelection.getActualBinding();
        }

        public Binding<?> getDefaultBinding() {
            return this.bindingSelection.getDefaultBinding();
        }

        public Key<java.util.Optional<T>> getKey() {
            return this.optionalKey;
        }

        public Set<Key<?>> getAlternateKeys() {
            Key<?> key = this.bindingSelection.getDirectKey();
            TypeLiteral<?> typeLiteral = key.getTypeLiteral();

            return ImmutableSet.of(key.ofType(RealOptionalBinder.javaOptionalOfProvider(typeLiteral)), key.ofType(RealOptionalBinder.javaOptionalOfJakartaProvider(typeLiteral)));
        }
    }

    private static final class JavaOptionalProviderProvider<T> extends RealOptionalBinder.RealOptionalBinderProviderWithDependencies<T, java.util.Optional<Provider<T>>> {

        private java.util.Optional<Provider<T>> value;

        JavaOptionalProviderProvider(RealOptionalBinder.BindingSelection<T> bindingSelection) {
            super(bindingSelection);
        }

        void doInitialize() {
            if (this.bindingSelection.getBinding() == null) {
                this.value = java.util.Optional.empty();
            } else {
                this.value = java.util.Optional.of(this.bindingSelection.getBinding().getProvider());
            }

        }

        protected java.util.Optional<Provider<T>> doProvision(InternalContext context, Dependency<?> dependency) {
            return this.value;
        }

        public Set<Dependency<?>> getDependencies() {
            return this.bindingSelection.providerDependencies();
        }
    }

    private static final class RealDirectTypeProvider<T> extends RealOptionalBinder.RealOptionalBinderProviderWithDependencies<T, T> {

        private Key<? extends T> targetKey;
        private InternalFactory<? extends T> targetFactory;

        RealDirectTypeProvider(RealOptionalBinder.BindingSelection<T> bindingSelection) {
            super(bindingSelection);
        }

        void doInitialize() {
            BindingImpl<T> targetBinding = this.bindingSelection.getBinding();

            this.targetKey = targetBinding.getKey();
            this.targetFactory = targetBinding.getInternalFactory();
        }

        protected T doProvision(InternalContext context, Dependency<?> dependency) throws InternalProvisionException {
            try {
                return this.targetFactory.get(context, dependency, true);
            } catch (InternalProvisionException internalprovisionexception) {
                throw internalprovisionexception.addSource(this.targetKey);
            }
        }

        public Set<Dependency<?>> getDependencies() {
            return this.bindingSelection.dependencies;
        }
    }

    private static final class RealOptionalProviderProvider<T> extends RealOptionalBinder.RealOptionalBinderProviderWithDependencies<T, Optional<Provider<T>>> {

        private Optional<Provider<T>> value;

        RealOptionalProviderProvider(RealOptionalBinder.BindingSelection<T> bindingSelection) {
            super(bindingSelection);
        }

        void doInitialize() {
            if (this.bindingSelection.getBinding() == null) {
                this.value = Optional.absent();
            } else {
                this.value = Optional.of(this.bindingSelection.getBinding().getProvider());
            }

        }

        protected Optional<Provider<T>> doProvision(InternalContext context, Dependency<?> dependency) {
            return this.value;
        }

        public Set<Dependency<?>> getDependencies() {
            return this.bindingSelection.providerDependencies();
        }
    }

    private static final class RealOptionalKeyProvider<T> extends RealOptionalBinder.RealOptionalBinderProviderWithDependencies<T, Optional<T>> implements ProviderWithExtensionVisitor<Optional<T>>, OptionalBinderBinding<Optional<T>> {

        private final Key<Optional<T>> optionalKey;
        private Dependency<?> targetDependency;
        private InternalFactory<? extends T> delegate;

        RealOptionalKeyProvider(RealOptionalBinder.BindingSelection<T> bindingSelection, Key<Optional<T>> optionalKey) {
            super(bindingSelection);
            this.optionalKey = optionalKey;
        }

        void doInitialize() {
            if (this.bindingSelection.getBinding() != null) {
                this.delegate = this.bindingSelection.getBinding().getInternalFactory();
                this.targetDependency = this.bindingSelection.getDependency();
            }

        }

        protected Optional<T> doProvision(InternalContext context, Dependency<?> currentDependency) throws InternalProvisionException {
            InternalFactory<? extends T> local = this.delegate;

            if (local == null) {
                return Optional.absent();
            } else {
                Dependency<?> localDependency = this.targetDependency;

                T result;

                try {
                    result = local.get(context, localDependency, false);
                } catch (InternalProvisionException internalprovisionexception) {
                    throw internalprovisionexception.addSource(localDependency);
                }

                return Optional.fromNullable(result);
            }
        }

        public Set<Dependency<?>> getDependencies() {
            return this.bindingSelection.dependencies();
        }

        public <B, R> R acceptExtensionVisitor(BindingTargetVisitor<B, R> visitor, ProviderInstanceBinding<? extends B> binding) {
            return (R) (visitor instanceof MultibindingsTargetVisitor ? ((MultibindingsTargetVisitor) visitor).visit(this) : visitor.visit(binding));
        }

        public Key<Optional<T>> getKey() {
            return this.optionalKey;
        }

        public Set<Key<?>> getAlternateKeys() {
            Key<?> key = this.bindingSelection.getDirectKey();
            TypeLiteral<?> typeLiteral = key.getTypeLiteral();

            return ImmutableSet.of(key.ofType(RealOptionalBinder.optionalOfProvider(typeLiteral)), key.ofType(RealOptionalBinder.optionalOfJakartaProvider(typeLiteral)));
        }

        public Binding<?> getActualBinding() {
            return this.bindingSelection.getActualBinding();
        }

        public Binding<?> getDefaultBinding() {
            return this.bindingSelection.getDefaultBinding();
        }

        public boolean containsElement(com.google.inject.spi.Element element) {
            return this.bindingSelection.containsElement(element);
        }
    }

    private static final class BindingSelection<T> {

        private static final ImmutableSet<Dependency<?>> MODULE_DEPENDENCIES = ImmutableSet.of(Dependency.get(Key.get(Injector.class)));
        BindingImpl<T> actualBinding;
        BindingImpl<T> defaultBinding;
        BindingImpl<T> binding;
        private boolean initialized;
        private final Key<T> key;
        private ImmutableSet<Dependency<?>> dependencies;
        private ImmutableSet<Dependency<?>> providerDependencies;
        private String bindingName;
        private Key<T> defaultBindingKey;
        private Key<T> actualBindingKey;

        BindingSelection(Key<T> key) {
            super();
            this.dependencies = RealOptionalBinder.BindingSelection.MODULE_DEPENDENCIES;
            this.providerDependencies = RealOptionalBinder.BindingSelection.MODULE_DEPENDENCIES;
            this.key = key;
        }

        void checkNotInitialized() {
            Errors.checkConfiguration(!this.initialized, "already initialized");
        }

        void initialize(InjectorImpl injector, Errors errors) {
            if (!this.initialized) {
                this.actualBinding = injector.getExistingBinding(this.getKeyForActualBinding());
                this.defaultBinding = injector.getExistingBinding(this.getKeyForDefaultBinding());
                BindingImpl<T> userBinding = injector.getExistingBinding(this.key);

                if (this.actualBinding != null) {
                    this.binding = this.actualBinding;
                } else if (this.defaultBinding != null) {
                    this.binding = this.defaultBinding;
                } else if (userBinding != null) {
                    this.binding = userBinding;
                    this.actualBinding = userBinding;
                }

                if (this.binding != null) {
                    this.dependencies = ImmutableSet.of(Dependency.get(this.binding.getKey()));
                    this.providerDependencies = ImmutableSet.of(Dependency.get(RealOptionalBinder.providerOf(this.binding.getKey())));
                } else {
                    this.dependencies = ImmutableSet.of();
                    this.providerDependencies = ImmutableSet.of();
                }

                this.checkBindingIsNotRecursive(errors);
                this.initialized = true;
            }
        }

        private void checkBindingIsNotRecursive(Errors errors) {
            if (this.binding instanceof LinkedBindingImpl) {
                LinkedBindingImpl<T> linkedBindingImpl = (LinkedBindingImpl) this.binding;

                if (linkedBindingImpl.getLinkedKey().equals(this.key)) {
                    errors.recursiveBinding(this.key, linkedBindingImpl.getLinkedKey());
                }
            }

        }

        Key<T> getKeyForDefaultBinding() {
            if (this.defaultBindingKey == null) {
                this.defaultBindingKey = this.key.withAnnotation(new RealOptionalBinder.DefaultImpl(this.getBindingName()));
            }

            return this.defaultBindingKey;
        }

        Key<T> getKeyForActualBinding() {
            if (this.actualBindingKey == null) {
                this.actualBindingKey = this.key.withAnnotation(new RealOptionalBinder.ActualImpl(this.getBindingName()));
            }

            return this.actualBindingKey;
        }

        Key<T> getDirectKey() {
            return this.key;
        }

        private String getBindingName() {
            if (this.bindingName == null) {
                this.bindingName = Annotations.nameOf(this.key);
            }

            return this.bindingName;
        }

        BindingImpl<T> getBinding() {
            return this.binding;
        }

        BindingImpl<T> getDefaultBinding() {
            return this.defaultBinding;
        }

        BindingImpl<T> getActualBinding() {
            return this.actualBinding;
        }

        ImmutableSet<Dependency<?>> providerDependencies() {
            return this.providerDependencies;
        }

        ImmutableSet<Dependency<?>> dependencies() {
            return this.dependencies;
        }

        Dependency<?> getDependency() {
            return (Dependency) Iterables.getOnlyElement(this.dependencies);
        }

        boolean containsElement(com.google.inject.spi.Element element) {
            if (element instanceof ProviderInstanceBinding) {
                jakarta.inject.Provider<?> providerInstance = ((ProviderInstanceBinding) element).getUserSuppliedProvider();

                if (providerInstance instanceof RealOptionalBinder.RealOptionalBinderProviderWithDependencies) {
                    return ((RealOptionalBinder.RealOptionalBinderProviderWithDependencies) providerInstance).bindingSelection.equals(this);
                }
            }

            if (!(element instanceof Binding)) {
                return false;
            } else {
                TypeLiteral<?> typeLiteral = this.key.getTypeLiteral();
                Key<?> elementKey = ((Binding) element).getKey();

                return elementKey.equals(this.getKeyForActualBinding()) || elementKey.equals(this.getKeyForDefaultBinding()) || elementKey.equals(this.key.ofType(RealOptionalBinder.javaOptionalOfJakartaProvider(typeLiteral))) || elementKey.equals(this.key.ofType(RealOptionalBinder.optionalOfJakartaProvider(typeLiteral)));
            }
        }

        public boolean equals(Object o) {
            return o instanceof RealOptionalBinder.BindingSelection && ((RealOptionalBinder.BindingSelection) o).key.equals(this.key);
        }

        public int hashCode() {
            return this.key.hashCode();
        }
    }

    private abstract static class RealOptionalBinderProviderWithDependencies<T, P> extends InternalProviderInstanceBindingImpl.Factory<P> {

        protected final RealOptionalBinder.BindingSelection<T> bindingSelection;
        private boolean initialized = false;

        RealOptionalBinderProviderWithDependencies(RealOptionalBinder.BindingSelection<T> bindingSelection) {
            super(InternalProviderInstanceBindingImpl.InitializationTiming.DELAYED);
            this.bindingSelection = bindingSelection;
        }

        final void initialize(InjectorImpl injector, Errors errors) throws ErrorsException {
            if (!this.initialized) {
                this.initialized = true;
                this.bindingSelection.initialize(injector, errors);
                this.doInitialize();
            }

        }

        abstract void doInitialize();

        public boolean equals(Object obj) {
            return obj != null && this.getClass() == obj.getClass() && this.bindingSelection.equals(((RealOptionalBinder.RealOptionalBinderProviderWithDependencies) obj).bindingSelection);
        }

        public int hashCode() {
            return this.bindingSelection.hashCode();
        }
    }

    static class DefaultImpl extends RealOptionalBinder.BaseAnnotation implements RealOptionalBinder.Default {

        public DefaultImpl(String value) {
            super(RealOptionalBinder.Default.class, value);
        }
    }

    static class ActualImpl extends RealOptionalBinder.BaseAnnotation implements RealOptionalBinder.Actual {

        public ActualImpl(String value) {
            super(RealOptionalBinder.Actual.class, value);
        }
    }

    abstract static class BaseAnnotation implements Serializable, Annotation {

        private final String value;
        private final Class<? extends Annotation> clazz;
        private static final long serialVersionUID = 0L;

        BaseAnnotation(Class<? extends Annotation> clazz, String value) {
            super();
            this.clazz = (Class) Preconditions.checkNotNull(clazz, "clazz");
            this.value = (String) Preconditions.checkNotNull(value, "value");
        }

        public String value() {
            return this.value;
        }

        public int hashCode() {
            return 127 * "value".hashCode() ^ this.value.hashCode();
        }

        public boolean equals(Object o) {
            if (o instanceof RealOptionalBinder.Actual && this.clazz == RealOptionalBinder.Actual.class) {
                RealOptionalBinder.Actual other = (RealOptionalBinder.Actual) o;

                return this.value.equals(other.value());
            } else if (o instanceof RealOptionalBinder.Default && this.clazz == RealOptionalBinder.Default.class) {
                RealOptionalBinder.Default other = (RealOptionalBinder.Default) o;

                return this.value.equals(other.value());
            } else {
                return false;
            }
        }

        public String toString() {
            return "@" + this.clazz.getName() + (this.value.isEmpty() ? "" : "(value=" + this.value + ")");
        }

        public Class<? extends Annotation> annotationType() {
            return this.clazz;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Qualifier
    @interface Actual {

        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Qualifier
    @interface Default {

        String value();
    }
}
