package com.google.inject.internal;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.DefaultBindingTargetVisitor;

abstract class AbstractBindingProcessor extends AbstractProcessor {

    private static final ImmutableSet<Class<?>> FORBIDDEN_TYPES = ImmutableSet.of(AbstractModule.class, Binder.class, Binding.class, Injector.class, Key.class, MembersInjector.class, new Class[]{Module.class, Provider.class, Scope.class, Stage.class, TypeLiteral.class});
    protected final ProcessedBindingData processedBindingData;

    AbstractBindingProcessor(Errors errors, ProcessedBindingData processedBindingData) {
        super(errors);
        this.processedBindingData = processedBindingData;
    }

    protected <T> UntargettedBindingImpl<T> invalidBinding(InjectorImpl injector, Key<T> key, Object source) {
        return new UntargettedBindingImpl<T>(injector, key, source);
    }

    protected void putBinding(BindingImpl<?> binding) {
        Key<?> key = binding.getKey();
        Class<?> rawType = key.getTypeLiteral().getRawType();

        if (AbstractBindingProcessor.FORBIDDEN_TYPES.contains(rawType)) {
            this.errors.cannotBindToGuiceType(rawType.getSimpleName());
        } else {
            BindingImpl<?> original = this.injector.getExistingBinding(key);

            if (original != null) {
                if (this.injector.getBindingData().getExplicitBinding(key) == null) {
                    this.errors.jitBindingAlreadySet(key);
                    return;
                }

                try {
                    if (!isOkayDuplicate(original, binding, this.injector.getBindingData())) {
                        this.errors.bindingAlreadySet(binding, original);
                        return;
                    }
                } catch (Throwable throwable) {
                    this.errors.errorCheckingDuplicateBinding(key, original.getSource(), throwable);
                    return;
                }
            }

            this.injector.getJitBindingData().banKeyInParent(key, this.injector.getBindingData(), binding.getSource());
            this.injector.getBindingData().putBinding(key, binding);
        }
    }

    private static boolean isOkayDuplicate(BindingImpl<?> original, BindingImpl<?> binding, InjectorBindingData bindingData) {
        if (original instanceof ExposedBindingImpl) {
            ExposedBindingImpl<?> exposed = (ExposedBindingImpl) original;
            InjectorImpl exposedFrom = (InjectorImpl) exposed.getPrivateElements().getInjector();

            return exposedFrom == binding.getInjector();
        } else {
            original = (BindingImpl) bindingData.getExplicitBindingsThisLevel().get(binding.getKey());
            return original == null ? false : original.equals(binding);
        }
    }

    private <T> void validateKey(Object source, Key<T> key) {
        Annotations.checkForMisplacedScopeAnnotations(key.getTypeLiteral().getRawType(), source, this.errors);
    }

    abstract class Processor<T, V> extends DefaultBindingTargetVisitor<T, V> {

        final Object source;
        final Key<T> key;
        final Class<? super T> rawType;
        Scoping scoping;

        Processor(BindingImpl<T> binding) {
            super();
            this.source = binding.getSource();
            this.key = binding.getKey();
            this.rawType = this.key.getTypeLiteral().getRawType();
            this.scoping = binding.getScoping();
        }

        protected void prepareBinding() {
            AbstractBindingProcessor.this.validateKey(this.source, this.key);
            this.scoping = Scoping.makeInjectable(this.scoping, AbstractBindingProcessor.this.injector, AbstractBindingProcessor.this.errors);
        }

        protected void scheduleInitialization(BindingImpl<?> binding) {
            AbstractBindingProcessor.this.processedBindingData.addUninitializedBinding(() -> {
                this.initializeBinding(binding);
            });
        }

        protected void scheduleDelayedInitialization(BindingImpl<?> binding) {
            AbstractBindingProcessor.this.processedBindingData.addDelayedUninitializedBinding(() -> {
                this.initializeBinding(binding);
            });
        }

        private void initializeBinding(BindingImpl<?> binding) {
            try {
                binding.getInjector().initializeBinding(binding, AbstractBindingProcessor.this.errors.withSource(this.source));
            } catch (ErrorsException errorsexception) {
                AbstractBindingProcessor.this.errors.merge(errorsexception.getErrors());
            }

        }
    }
}
