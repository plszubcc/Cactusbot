package com.google.inject.internal;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.google.inject.Key;
import com.google.inject.internal.util.SourceProvider;
import java.util.Map;
import java.util.Set;

final class WeakKeySet {

    private Map<Key<?>, Multiset<Object>> backingMap;
    private final Object lock;
    private final Cache<InjectorBindingData, Set<WeakKeySet.KeyAndSource>> evictionCache = CacheBuilder.newBuilder().weakKeys().removalListener(this::cleanupOnRemoval).build();

    private void cleanupOnRemoval(RemovalNotification<InjectorBindingData, Set<WeakKeySet.KeyAndSource>> notification) {
        Preconditions.checkState(RemovalCause.COLLECTED.equals(notification.getCause()));
        synchronized (this.lock) {
            for (WeakKeySet.KeyAndSource keyAndSource : (Set) notification.getValue()) {
                Multiset<Object> set = (Multiset) this.backingMap.get(keyAndSource.key);

                if (set != null) {
                    set.remove(keyAndSource.source);
                    if (set.isEmpty()) {
                        this.backingMap.remove(keyAndSource.key);
                    }
                }
            }

        }
    }

    WeakKeySet(Object lock) {
        super();
        this.lock = lock;
    }

    public void add(Key<?> key, InjectorBindingData state, Object source) {
        if (this.backingMap == null) {
            this.backingMap = Maps.newHashMap();
        }

        if (source instanceof Class || source == SourceProvider.UNKNOWN_SOURCE) {
            source = null;
        }

        Object convertedSource = Errors.convert(source);

        ((Multiset) this.backingMap.computeIfAbsent(key, (kx) -> {
            return LinkedHashMultiset.create();
        })).add(convertedSource);
        if (state.parent().isPresent()) {
            Set<WeakKeySet.KeyAndSource> keyAndSources = (Set) this.evictionCache.getIfPresent(state);

            if (keyAndSources == null) {
                this.evictionCache.put(state, keyAndSources = Sets.newHashSet());
            }

            keyAndSources.add(new WeakKeySet.KeyAndSource(key, convertedSource));
        }

    }

    public boolean contains(Key<?> key) {
        this.evictionCache.cleanUp();
        return this.backingMap != null && this.backingMap.containsKey(key);
    }

    public Set<Object> getSources(Key<?> key) {
        this.evictionCache.cleanUp();
        Multiset<Object> sources = this.backingMap == null ? null : (Multiset) this.backingMap.get(key);

        return sources == null ? null : sources.elementSet();
    }

    private static final class KeyAndSource {

        final Key<?> key;
        final Object source;

        KeyAndSource(Key<?> key, Object source) {
            super();
            this.key = key;
            this.source = source;
        }

        public int hashCode() {
            return Objects.hashCode(new Object[]{this.key, this.source});
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (!(obj instanceof WeakKeySet.KeyAndSource)) {
                return false;
            } else {
                WeakKeySet.KeyAndSource other = (WeakKeySet.KeyAndSource) obj;

                return Objects.equal(this.key, other.key) && Objects.equal(this.source, other.source);
            }
        }
    }
}
