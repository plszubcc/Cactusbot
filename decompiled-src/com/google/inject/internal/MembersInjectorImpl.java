package com.google.inject.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.InjectionPoint;
import javax.annotation.Nullable;

final class MembersInjectorImpl<T> implements MembersInjector<T> {

    private final TypeLiteral<T> typeLiteral;
    private final InjectorImpl injector;
    @Nullable
    private final ImmutableList<SingleMemberInjector> memberInjectors;
    @Nullable
    private final ImmutableList<MembersInjector<? super T>> userMembersInjectors;
    @Nullable
    private final ImmutableList<InjectionListener<? super T>> injectionListeners;
    @Nullable
    private final ImmutableList<MethodAspect> addedAspects;

    MembersInjectorImpl(InjectorImpl injector, TypeLiteral<T> typeLiteral, EncounterImpl<T> encounter, ImmutableList<SingleMemberInjector> memberInjectors) {
        super();
        this.injector = injector;
        this.typeLiteral = typeLiteral;
        this.memberInjectors = memberInjectors.isEmpty() ? null : memberInjectors;
        this.userMembersInjectors = encounter.getMembersInjectors().isEmpty() ? null : encounter.getMembersInjectors().asList();
        this.injectionListeners = encounter.getInjectionListeners().isEmpty() ? null : encounter.getInjectionListeners().asList();
        this.addedAspects = InternalFlags.isBytecodeGenEnabled() && !encounter.getAspects().isEmpty() ? encounter.getAspects() : null;
    }

    public ImmutableList<SingleMemberInjector> getMemberInjectors() {
        return this.memberInjectors == null ? ImmutableList.of() : this.memberInjectors;
    }

    public void injectMembers(T instance) {
        TypeLiteral<T> localTypeLiteral = this.typeLiteral;

        try {
            this.injectAndNotify(instance, (Key) null, (ProvisionListenerStackCallback) null, localTypeLiteral, false);
        } catch (InternalProvisionException internalprovisionexception) {
            throw internalprovisionexception.addSource(localTypeLiteral).toProvisionException();
        }
    }

    void injectAndNotify(final T instance, Key<T> key, ProvisionListenerStackCallback<T> provisionCallback, Object source, final boolean toolableOnly) throws InternalProvisionException {
        if (instance != null) {
            final InternalContext context = this.injector.enterContext();

            try {
                if (provisionCallback != null && provisionCallback.hasListeners()) {
                    provisionCallback.provision(context, new ProvisionListenerStackCallback.ProvisionCallback<T>() {
                        public T call() throws InternalProvisionException {
                            MembersInjectorImpl.this.injectMembers(instance, context, toolableOnly);
                            return instance;
                        }
                    });
                } else {
                    this.injectMembers(instance, context, toolableOnly);
                }
            } finally {
                context.close();
            }

            if (!toolableOnly) {
                this.notifyListeners(instance);
            }

        }
    }

    void notifyListeners(T instance) throws InternalProvisionException {
        ImmutableList<InjectionListener<? super T>> localInjectionListeners = this.injectionListeners;

        if (localInjectionListeners != null) {
            for (int i = 0; i < localInjectionListeners.size(); ++i) {
                InjectionListener<? super T> injectionListener = (InjectionListener) localInjectionListeners.get(i);

                try {
                    injectionListener.afterInjection(instance);
                } catch (RuntimeException runtimeexception) {
                    throw InternalProvisionException.errorNotifyingInjectionListener(injectionListener, this.typeLiteral, runtimeexception);
                }
            }

        }
    }

    void injectMembers(T t, InternalContext context, boolean toolableOnly) throws InternalProvisionException {
        ImmutableList<SingleMemberInjector> localMembersInjectors = this.memberInjectors;

        if (localMembersInjectors != null) {
            int i = 0;

            for (int size = localMembersInjectors.size(); i < size; ++i) {
                SingleMemberInjector injector = (SingleMemberInjector) localMembersInjectors.get(i);

                if (!toolableOnly || injector.getInjectionPoint().isToolable()) {
                    injector.inject(context, t);
                }
            }
        }

        if (!toolableOnly) {
            ImmutableList<MembersInjector<? super T>> localUsersMembersInjectors = this.userMembersInjectors;

            if (localUsersMembersInjectors != null) {
                for (int i = 0; i < localUsersMembersInjectors.size(); ++i) {
                    MembersInjector<? super T> userMembersInjector = (MembersInjector) localUsersMembersInjectors.get(i);

                    try {
                        userMembersInjector.injectMembers(t);
                    } catch (RuntimeException runtimeexception) {
                        throw InternalProvisionException.errorInUserInjector(userMembersInjector, this.typeLiteral, runtimeexception);
                    }
                }
            }
        }

    }

    public String toString() {
        return "MembersInjector<" + this.typeLiteral + ">";
    }

    public ImmutableSet<InjectionPoint> getInjectionPoints() {
        ImmutableList<SingleMemberInjector> localMemberInjectors = this.memberInjectors;

        if (localMemberInjectors == null) {
            return ImmutableSet.of();
        } else {
            ImmutableSet.Builder<InjectionPoint> builder = ImmutableSet.builder();
            UnmodifiableIterator unmodifiableiterator = localMemberInjectors.iterator();

            while (unmodifiableiterator.hasNext()) {
                SingleMemberInjector memberInjector = (SingleMemberInjector) unmodifiableiterator.next();

                builder.add(memberInjector.getInjectionPoint());
            }

            return builder.build();
        }
    }

    public ImmutableList<MethodAspect> getAddedAspects() {
        return this.addedAspects == null ? ImmutableList.of() : this.addedAspects;
    }
}
