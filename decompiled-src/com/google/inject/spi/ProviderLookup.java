package com.google.inject.spi;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.internal.Errors;
import com.google.inject.util.Types;
import java.util.Set;

public final class ProviderLookup<T> implements Element {

    private final Object source;
    private final Dependency<T> dependency;
    private Provider<T> delegate;

    public ProviderLookup(Object source, Key<T> key) {
        this(source, Dependency.get((Key) Preconditions.checkNotNull(key, "key")));
    }

    public ProviderLookup(Object source, Dependency<T> dependency) {
        super();
        this.source = Preconditions.checkNotNull(source, "source");
        this.dependency = (Dependency) Preconditions.checkNotNull(dependency, "dependency");
    }

    public Object getSource() {
        return this.source;
    }

    public Key<T> getKey() {
        return this.dependency.getKey();
    }

    public Dependency<T> getDependency() {
        return this.dependency;
    }

    public <T> T acceptVisitor(ElementVisitor<T> visitor) {
        return (T) visitor.visit(this);
    }

    public void initializeDelegate(Provider<T> delegate) {
        Preconditions.checkState(this.delegate == null, "delegate already initialized");
        this.delegate = (Provider) Preconditions.checkNotNull(delegate, "delegate");
    }

    public void applyTo(Binder binder) {
        this.initializeDelegate(binder.withSource(this.getSource()).getProvider(this.dependency));
    }

    public Provider<T> getDelegate() {
        return this.delegate;
    }

    public Provider<T> getProvider() {
        return new ProviderWithDependencies<T>() {
            public T get() {
                Provider<T> local = ProviderLookup.this.delegate;

                if (local == null) {
                    throw new IllegalStateException("This Provider cannot be used until the Injector has been created.");
                } else {
                    return (T) local.get();
                }
            }

            public Set<Dependency<?>> getDependencies() {
                Key<?> providerKey = ProviderLookup.this.getKey().ofType(Types.providerOf(ProviderLookup.this.getKey().getTypeLiteral().getType()));

                return ImmutableSet.of(Dependency.get(providerKey));
            }

            public String toString() {
                return "Provider<" + ProviderLookup.this.getKey().getTypeLiteral() + ">";
            }
        };
    }

    public String toString() {
        return MoreObjects.toStringHelper(ProviderLookup.class).add("dependency", this.dependency).add("source", Errors.convert(this.source)).toString();
    }

    public boolean equals(Object obj) {
        return obj instanceof ProviderLookup && ((ProviderLookup) obj).dependency.equals(this.dependency) && ((ProviderLookup) obj).source.equals(this.source);
    }

    public int hashCode() {
        return Objects.hashCode(new Object[]{this.dependency, this.source});
    }
}
