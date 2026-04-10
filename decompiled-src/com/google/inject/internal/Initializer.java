package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionPoint;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

final class Initializer {

    private volatile boolean validationStarted = false;
    private final CycleDetectingLock.CycleDetectingLockFactory<Class<?>> cycleDetectingLockFactory = new CycleDetectingLock.CycleDetectingLockFactory<Class<?>>();
    private final List<Initializer.InjectableReference<?>> pendingInjections = Lists.newArrayList();
    private final IdentityHashMap<Object, Initializer.InjectableReference<?>> initializablesCache = Maps.newIdentityHashMap();

    Initializer() {
        super();
    }

    <T> Initializable<T> requestInjection(InjectorImpl injector, TypeLiteral<T> type, T instance, Binding<T> binding, Object source, Set<InjectionPoint> injectionPoints, Errors errors) {
        Preconditions.checkNotNull(source);
        Preconditions.checkState(!this.validationStarted, "Member injection could not be requested after validation is started");
        ProvisionListenerStackCallback<T> provisionCallback = binding == null ? null : injector.provisionListenerStore.get(binding);

        if (instance != null && (!injectionPoints.isEmpty() || injector.membersInjectorStore.hasTypeListeners() || provisionCallback != null)) {
            if (type == null) {
                TypeLiteral<T> instanceType = TypeLiteral.get(instance.getClass());

                type = instanceType;
            }

            if (this.initializablesCache.containsKey(instance)) {
                Initializer.InjectableReference<T> cached = (Initializer.InjectableReference) this.initializablesCache.get(instance);

                if (!cached.type.equals(type)) {
                    errors.requestInjectionWithDifferentTypes(instance, cached.type, cached.source, type);
                }

                return cached;
            } else {
                Initializer.InjectableReference<T> injectableReference = new Initializer.InjectableReference<T>(injector, instance, type, binding == null ? null : binding.getKey(), provisionCallback, source, this.cycleDetectingLockFactory.create(instance.getClass()));

                this.initializablesCache.put(instance, injectableReference);
                this.pendingInjections.add(injectableReference);
                return injectableReference;
            }
        } else {
            return Initializables.<T>of(instance);
        }
    }

    void validateOustandingInjections(Errors errors) {
        this.validationStarted = true;
        this.initializablesCache.clear();

        for (Initializer.InjectableReference<?> reference : this.pendingInjections) {
            try {
                reference.validate(errors);
            } catch (ErrorsException errorsexception) {
                errors.merge(errorsexception.getErrors());
            }
        }

    }

    void injectAll(Errors errors) {
        Preconditions.checkState(this.validationStarted, "Validation should be done before injection");

        for (Initializer.InjectableReference<?> reference : this.pendingInjections) {
            try {
                reference.get();
            } catch (InternalProvisionException internalprovisionexception) {
                errors.merge(internalprovisionexception);
            }
        }

        this.pendingInjections.clear();
    }

    private static enum InjectableReferenceState {

        NEW, VALIDATED, INJECTING, READY;

        private InjectableReferenceState() {}
    }

    private static class InjectableReference<T> implements Initializable<T> {

        private volatile Initializer.InjectableReferenceState state;
        private volatile MembersInjectorImpl<T> membersInjector;
        private final InjectorImpl injector;
        private final T instance;
        private final TypeLiteral<T> type;
        private final Object source;
        private final Key<T> key;
        private final ProvisionListenerStackCallback<T> provisionCallback;
        private final CycleDetectingLock<?> lock;

        public InjectableReference(InjectorImpl injector, T instance, TypeLiteral<T> type, Key<T> key, ProvisionListenerStackCallback<T> provisionCallback, Object source, CycleDetectingLock<?> lock) {
            super();
            this.state = Initializer.InjectableReferenceState.NEW;
            this.membersInjector = null;
            this.injector = injector;
            this.key = key;
            this.provisionCallback = provisionCallback;
            this.instance = (T) Preconditions.checkNotNull(instance, "instance");
            this.type = (TypeLiteral) Preconditions.checkNotNull(type, "type");
            this.source = Preconditions.checkNotNull(source, "source");
            this.lock = (CycleDetectingLock) Preconditions.checkNotNull(lock, "lock");
        }

        public void validate(Errors errors) throws ErrorsException {
            this.membersInjector = this.injector.membersInjectorStore.<T>get(this.type, errors.withSource(this.source));
            Preconditions.checkNotNull(this.membersInjector, "No membersInjector available for type: %s, from key: %s", this.type, this.key);
            this.state = Initializer.InjectableReferenceState.VALIDATED;
        }

        public T get() throws InternalProvisionException {
            if (this.state == Initializer.InjectableReferenceState.READY) {
                return this.instance;
            } else {
                Multimap<?, ?> lockCycle = this.lock.lockOrDetectPotentialLocksCycle();

                if (!lockCycle.isEmpty()) {
                    return this.instance;
                } else {
                    try {
                        switch (this.state) {
                            case READY:
                                Object object = this.instance;

                                return (T) object;
                            case INJECTING:
                                Object object1 = this.instance;

                                return (T) object1;
                            case VALIDATED:
                                this.state = Initializer.InjectableReferenceState.INJECTING;

                                try {
                                    this.membersInjector.injectAndNotify(this.instance, this.key, this.provisionCallback, this.source, this.injector.options.stage == Stage.TOOL);
                                } catch (InternalProvisionException internalprovisionexception) {
                                    throw internalprovisionexception.addSource(this.source);
                                }

                                this.state = Initializer.InjectableReferenceState.READY;
                                Object ipe = this.instance;

                                return (T) ipe;
                            case NEW:
                                throw new IllegalStateException("InjectableReference is not validated yet");
                            default:
                                throw new IllegalStateException("Unknown state: " + this.state);
                        }
                    } finally {
                        this.lock.unlock();
                    }
                }
            }
        }

        public String toString() {
            return this.instance.toString();
        }
    }
}
