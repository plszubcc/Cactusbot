package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.Scope;
import com.google.inject.Scopes;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.Message;
import java.util.Formatter;
import java.util.List;
import javax.annotation.Nullable;

public class SingletonScope implements Scope {

    private static final Object NULL = new Object();
    private static final CycleDetectingLock.CycleDetectingLockFactory<Key<?>> cycleDetectingLockFactory = new CycleDetectingLock.CycleDetectingLockFactory<Key<?>>();

    public SingletonScope() {
        super();
    }

    public <T> Provider<T> scope(final Key<T> key, final Provider<T> creator) {
        return new Provider<T>() {
            volatile Object instance;
            final ConstructionContext<T> constructionContext = new ConstructionContext<T>();
            final CycleDetectingLock<Key<?>> creationLock;
            @Nullable
            final InjectorImpl injector;

            {
                this.creationLock = SingletonScope.cycleDetectingLockFactory.create(key);
                if (creator instanceof ProviderToInternalFactoryAdapter) {
                    this.injector = ((ProviderToInternalFactoryAdapter) creator).getInjector();
                } else {
                    this.injector = null;
                }

            }

            public T get() {
                Object initialInstance = this.instance;

                if (initialInstance == null) {
                    InternalContext context = this.injector == null ? null : this.injector.getLocalContext();
                    ListMultimap<Thread, Key<?>> locksCycle = this.creationLock.lockOrDetectPotentialLocksCycle();

                    if (locksCycle.isEmpty()) {
                        try {
                            if (this.instance == null) {
                                T provided = (T) creator.get();
                                Object providedNotNull = provided == null ? SingletonScope.NULL : provided;

                                if (this.instance == null) {
                                    if (Scopes.isCircularProxy(provided)) {
                                        Object object = provided;

                                        return (T) object;
                                    }

                                    synchronized (this.constructionContext) {
                                        this.instance = providedNotNull;
                                        this.constructionContext.setProxyDelegates(provided);
                                    }
                                } else {
                                    Preconditions.checkState(this.instance == providedNotNull, "Singleton is called recursively returning different results");
                                }
                            }
                        } catch (RuntimeException runtimeexception) {
                            synchronized (this.constructionContext) {
                                this.constructionContext.finishConstruction();
                            }

                            throw runtimeexception;
                        } finally {
                            this.creationLock.unlock();
                        }
                    } else {
                        if (context == null) {
                            throw new ProvisionException(ImmutableList.of(this.createCycleDependenciesMessage(locksCycle, (Message) null)));
                        }

                        synchronized (this.constructionContext) {
                            if (this.instance == null) {
                                Dependency<?> dependency = (Dependency) Preconditions.checkNotNull(context.getDependency(), "internalContext.getDependency()");
                                Class<?> rawType = dependency.getKey().getTypeLiteral().getRawType();

                                Object object1;

                                try {
                                    T proxy = (T) this.constructionContext.createProxy(context.getInjectorOptions(), rawType);

                                    object1 = proxy;
                                } catch (InternalProvisionException internalprovisionexception) {
                                    Message proxyCreationError = (Message) Iterables.getOnlyElement(internalprovisionexception.getErrors());
                                    Message cycleDependenciesMessage = this.createCycleDependenciesMessage(locksCycle, proxyCreationError);

                                    throw new ProvisionException(ImmutableList.of(cycleDependenciesMessage, proxyCreationError));
                                }

                                return (T) object1;
                            }
                        }
                    }

                    Object initializedInstance = this.instance;

                    Preconditions.checkState(initializedInstance != null, "Internal error: Singleton is not initialized contrary to our expectations");
                    return (T) (initializedInstance == SingletonScope.NULL ? null : initializedInstance);
                } else {
                    return (T) (initialInstance == SingletonScope.NULL ? null : initialInstance);
                }
            }

            private Message createCycleDependenciesMessage(ListMultimap<Thread, Key<?>> locksCycle, @Nullable Message proxyCreationError) {
                StringBuilder sb = new StringBuilder();
                Formatter fmt = new Formatter(sb);

                fmt.format("Encountered circular dependency spanning several threads.");
                if (proxyCreationError != null) {
                    fmt.format(" %s", proxyCreationError.getMessage());
                }

                fmt.format("\n");

                for (Thread lockedThread : locksCycle.keySet()) {
                    List<Key<?>> lockedKeys = locksCycle.get(lockedThread);

                    fmt.format("%s is holding locks the following singletons in the cycle:\n", lockedThread);

                    for (Key<?> lockedKey : lockedKeys) {
                        fmt.format("%s\n", Errors.convert(lockedKey));
                    }

                    for (StackTraceElement traceElement : lockedThread.getStackTrace()) {
                        fmt.format("\tat %s\n", traceElement);
                    }
                }

                fmt.close();
                return new Message(Thread.currentThread(), sb.toString());
            }

            public String toString() {
                return String.format("%s[%s]", creator, Scopes.SINGLETON);
            }
        };
    }

    public String toString() {
        return "Scopes.SINGLETON";
    }
}
