package com.google.inject.internal;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class FailableCache<K, V> {

    private final Set<K> loadingSet = ConcurrentHashMap.newKeySet();
    private final LoadingCache<K, Object> delegate = CacheBuilder.newBuilder().build(new CacheLoader<K, Object>() {
        public Object load(K key) {
            FailableCache.this.loadingSet.add(key);
            Errors errors = new Errors();
            V result = (V) null;

            try {
                result = (V) FailableCache.this.create(key, errors);
            } catch (ErrorsException errorsexception) {
                errors.merge(errorsexception.getErrors());
            } finally {
                FailableCache.this.loadingSet.remove(key);
            }

            return errors.hasErrors() ? errors : result;
        }
    });

    public FailableCache() {
        super();
    }

    protected abstract V create(K key, Errors errors) throws ErrorsException;

    public V get(K key, Errors errors) throws ErrorsException {
        Object resultOrError = this.delegate.getUnchecked(key);

        if (resultOrError instanceof Errors) {
            errors.merge((Errors) resultOrError);
            throw errors.toException();
        } else {
            return (V) resultOrError;
        }
    }

    boolean remove(K key) {
        return this.delegate.asMap().remove(key) != null;
    }

    boolean isLoading(K key) {
        return this.loadingSet.contains(key);
    }

    Map<K, V> asMap() {
        return Maps.transformValues(Maps.filterValues(ImmutableMap.copyOf(this.delegate.asMap()), (resultOrErrorx) -> {
            return !(resultOrErrorx instanceof Errors);
        }), (resultOrErrorx) -> {
            return resultOrErrorx;
        });
    }
}
