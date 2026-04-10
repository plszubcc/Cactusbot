package com.google.inject.internal;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.PrivateBinder;
import com.google.inject.spi.ElementVisitor;
import com.google.inject.spi.PrivateElements;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class PrivateElementsImpl implements PrivateElements {

    private final Object source;
    private List<com.google.inject.spi.Element> elementsMutable = Lists.newArrayList();
    private List<ExposureBuilder<?>> exposureBuilders = Lists.newArrayList();
    private ImmutableList<com.google.inject.spi.Element> elements;
    private ImmutableMap<Key<?>, Object> exposedKeysToSources;
    private Injector injector;

    public PrivateElementsImpl(Object source) {
        super();
        this.source = Preconditions.checkNotNull(source, "source");
    }

    public Object getSource() {
        return this.source;
    }

    public List<com.google.inject.spi.Element> getElements() {
        if (this.elements == null) {
            this.elements = ImmutableList.copyOf(this.elementsMutable);
            this.elementsMutable = null;
        }

        return this.elements;
    }

    public Injector getInjector() {
        return this.injector;
    }

    public void initInjector(Injector injector) {
        Preconditions.checkState(this.injector == null, "injector already initialized");
        this.injector = (Injector) Preconditions.checkNotNull(injector, "injector");
    }

    public Set<Key<?>> getExposedKeys() {
        if (this.exposedKeysToSources == null) {
            Map<Key<?>, Object> exposedKeysToSourcesMutable = Maps.newLinkedHashMap();

            for (ExposureBuilder<?> exposureBuilder : this.exposureBuilders) {
                exposedKeysToSourcesMutable.put(exposureBuilder.getKey(), exposureBuilder.getSource());
            }

            this.exposedKeysToSources = ImmutableMap.copyOf(exposedKeysToSourcesMutable);
            this.exposureBuilders = null;
        }

        return this.exposedKeysToSources.keySet();
    }

    public <T> T acceptVisitor(ElementVisitor<T> visitor) {
        return (T) visitor.visit(this);
    }

    public List<com.google.inject.spi.Element> getElementsMutable() {
        return this.elementsMutable;
    }

    public void addExposureBuilder(ExposureBuilder<?> exposureBuilder) {
        this.exposureBuilders.add(exposureBuilder);
    }

    public void applyTo(Binder binder) {
        PrivateBinder privateBinder = binder.withSource(this.source).newPrivateBinder();

        for (com.google.inject.spi.Element element : this.getElements()) {
            element.applyTo(privateBinder);
        }

        this.getExposedKeys();
        UnmodifiableIterator unmodifiableiterator = this.exposedKeysToSources.entrySet().iterator();

        while (unmodifiableiterator.hasNext()) {
            Map.Entry<Key<?>, Object> entry = (Entry) unmodifiableiterator.next();

            privateBinder.withSource(entry.getValue()).expose((Key) entry.getKey());
        }

    }

    public Object getExposedSource(Key<?> key) {
        this.getExposedKeys();
        Object source = this.exposedKeysToSources.get(key);

        Preconditions.checkArgument(source != null, "%s not exposed by %s.", key, this);
        return source;
    }

    public String toString() {
        return MoreObjects.toStringHelper(PrivateElements.class).add("exposedKeys", this.getExposedKeys()).add("source", this.getSource()).toString();
    }
}
