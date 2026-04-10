package com.google.inject.internal;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.ConstructorBinding;
import com.google.inject.spi.ConvertedConstantBinding;
import com.google.inject.spi.ExposedBinding;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.InstanceBinding;
import com.google.inject.spi.LinkedKeyBinding;
import com.google.inject.spi.PrivateElements;
import com.google.inject.spi.ProviderBinding;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.ProviderKeyBinding;
import com.google.inject.spi.UntargettedBinding;
import jakarta.inject.Provider;
import java.util.Set;

final class BindingProcessor extends AbstractBindingProcessor {

    private final Initializer initializer;

    BindingProcessor(Errors errors, Initializer initializer, ProcessedBindingData processedBindingData) {
        super(errors, processedBindingData);
        this.initializer = initializer;
    }

    public <T> Boolean visit(Binding<T> command) {
        Class<?> rawType = command.getKey().getTypeLiteral().getRawType();

        if (!Void.class.equals(rawType)) {
            if (rawType == com.google.inject.Provider.class) {
                this.errors.bindingToProvider();
                return true;
            } else {
                return (Boolean) command.acceptTargetVisitor(new AbstractBindingProcessor.Processor<T, Boolean>((BindingImpl) command) {
                    public Boolean visit(ConstructorBinding<? extends T> binding) {
                        this.prepareBinding();

                        try {
                            ConstructorBindingImpl<T> onInjector = ConstructorBindingImpl.<T>create(BindingProcessor.this.injector, this.key, binding.getConstructor(), this.source, this.scoping, BindingProcessor.this.errors, false, false);

                            this.scheduleInitialization(onInjector);
                            BindingProcessor.this.putBinding(onInjector);
                        } catch (ErrorsException errorsexception) {
                            BindingProcessor.this.errors.merge(errorsexception.getErrors());
                            BindingProcessor.this.putBinding(BindingProcessor.this.invalidBinding(BindingProcessor.this.injector, this.key, this.source));
                        }

                        return true;
                    }

                    public Boolean visit(InstanceBinding<? extends T> binding) {
                        this.prepareBinding();
                        Set<InjectionPoint> injectionPoints = binding.getInjectionPoints();
                        T instance = binding.getInstance();
                        Initializable<T> ref = BindingProcessor.this.initializer.<T>requestInjection(BindingProcessor.this.injector, (TypeLiteral) null, instance, binding, this.source, injectionPoints, BindingProcessor.this.errors);
                        ConstantFactory<? extends T> factory = new ConstantFactory<T>(ref);
                        InternalFactory<? extends T> scopedFactory = Scoping.<T>scope(this.key, BindingProcessor.this.injector, factory, this.source, this.scoping);

                        BindingProcessor.this.putBinding(new InstanceBindingImpl(BindingProcessor.this.injector, this.key, this.source, scopedFactory, injectionPoints, instance));
                        return true;
                    }

                    public Boolean visit(ProviderInstanceBinding<? extends T> binding) {
                        this.prepareBinding();
                        Provider<? extends T> provider = binding.getUserSuppliedProvider();

                        if (provider instanceof InternalProviderInstanceBindingImpl.Factory) {
                            InternalProviderInstanceBindingImpl.Factory<T> asProviderMethod = (InternalProviderInstanceBindingImpl.Factory) provider;

                            return this.visitInternalProviderInstanceBindingFactory(asProviderMethod);
                        } else {
                            Set<InjectionPoint> injectionPoints = binding.getInjectionPoints();
                            Initializable<? extends Provider<? extends T>> initializable = BindingProcessor.this.initializer.<Provider<? extends T>>requestInjection(BindingProcessor.this.injector, (TypeLiteral) null, provider, (Binding) null, this.source, injectionPoints, BindingProcessor.this.errors);
                            InternalFactory<T> factory = new InternalFactoryToInitializableAdapter<T>(initializable, this.source, BindingProcessor.this.injector.provisionListenerStore.get(binding));
                            InternalFactory<? extends T> scopedFactory = Scoping.<T>scope(this.key, BindingProcessor.this.injector, factory, this.source, this.scoping);

                            BindingProcessor.this.putBinding(new ProviderInstanceBindingImpl(BindingProcessor.this.injector, this.key, this.source, scopedFactory, this.scoping, provider, injectionPoints));
                            return true;
                        }
                    }

                    public Boolean visit(ProviderKeyBinding<? extends T> binding) {
                        this.prepareBinding();
                        Key<? extends Provider<? extends T>> providerKey = binding.getProviderKey();
                        BoundProviderFactory<T> boundProviderFactory = new BoundProviderFactory<T>(BindingProcessor.this.injector, providerKey, this.source, BindingProcessor.this.injector.provisionListenerStore.get(binding));

                        BindingProcessor.this.processedBindingData.addCreationListener(boundProviderFactory);
                        InternalFactory<? extends T> scopedFactory = Scoping.<T>scope(this.key, BindingProcessor.this.injector, boundProviderFactory, this.source, this.scoping);

                        BindingProcessor.this.putBinding(new LinkedProviderBindingImpl(BindingProcessor.this.injector, this.key, this.source, scopedFactory, this.scoping, providerKey));
                        return true;
                    }

                    public Boolean visit(LinkedKeyBinding<? extends T> binding) {
                        this.prepareBinding();
                        Key<? extends T> linkedKey = binding.getLinkedKey();

                        if (this.key.equals(linkedKey)) {
                            BindingProcessor.this.errors.recursiveBinding(this.key, linkedKey);
                        }

                        FactoryProxy<T> factory = new FactoryProxy<T>(BindingProcessor.this.injector, this.key, linkedKey, this.source);

                        BindingProcessor.this.processedBindingData.addCreationListener(factory);
                        InternalFactory<? extends T> scopedFactory = Scoping.<T>scope(this.key, BindingProcessor.this.injector, factory, this.source, this.scoping);

                        BindingProcessor.this.putBinding(new LinkedBindingImpl(BindingProcessor.this.injector, this.key, this.source, scopedFactory, this.scoping, linkedKey));
                        return true;
                    }

                    private Boolean visitInternalProviderInstanceBindingFactory(InternalProviderInstanceBindingImpl.Factory<T> provider) {
                        InternalProviderInstanceBindingImpl<T> binding = new InternalProviderInstanceBindingImpl<T>(BindingProcessor.this.injector, this.key, this.source, provider, Scoping.scope(this.key, BindingProcessor.this.injector, provider, this.source, this.scoping), this.scoping);

                        switch (binding.getInitializationTiming()) {
                            case DELAYED:
                                this.scheduleDelayedInitialization(binding);
                                break;
                            case EAGER:
                                this.scheduleInitialization(binding);
                                break;
                            default:
                                throw new AssertionError();
                        }

                        BindingProcessor.this.putBinding(binding);
                        return true;
                    }

                    public Boolean visit(UntargettedBinding<? extends T> untargetted) {
                        return false;
                    }

                    public Boolean visit(ExposedBinding<? extends T> binding) {
                        throw new IllegalArgumentException("Cannot apply a non-module element");
                    }

                    public Boolean visit(ConvertedConstantBinding<? extends T> binding) {
                        throw new IllegalArgumentException("Cannot apply a non-module element");
                    }

                    public Boolean visit(ProviderBinding<? extends T> binding) {
                        throw new IllegalArgumentException("Cannot apply a non-module element");
                    }

                    protected Boolean visitOther(Binding<? extends T> binding) {
                        throw new IllegalStateException("BindingProcessor should override all visitations");
                    }
                });
            }
        } else {
            if (command instanceof ProviderInstanceBinding && ((ProviderInstanceBinding) command).getUserSuppliedProvider() instanceof ProviderMethod) {
                this.errors.voidProviderMethod();
            } else {
                this.errors.missingConstantValues();
            }

            return true;
        }
    }

    public Boolean visit(PrivateElements privateElements) {
        for (Key<?> key : privateElements.getExposedKeys()) {
            this.bindExposed(privateElements, key);
        }

        return false;
    }

    private <T> void bindExposed(PrivateElements privateElements, Key<T> key) {
        ExposedKeyFactory<T> exposedKeyFactory = new ExposedKeyFactory<T>(key, privateElements);

        this.processedBindingData.addCreationListener(exposedKeyFactory);
        this.putBinding(new ExposedBindingImpl(this.injector, privateElements.getExposedSource(key), key, exposedKeyFactory, privateElements));
    }
}
