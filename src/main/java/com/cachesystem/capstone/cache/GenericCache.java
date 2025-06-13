package com.cachesystem.capstone.cache;

import com.cachesystem.capstone.eviction.EvictionPolicy;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generic Cache implementation using an eviction policy.
 */
public class GenericCache<K, V> implements Cache<K, V> {
    private final int capacity;
    private final Map<K, V> storage;
    private final EvictionPolicy<K> evictionPolicy;
    protected final Map<K, V> cacheMap = new LinkedHashMap<>();

    public GenericCache(int capacity, EvictionPolicy<K> evictionPolicy) {
        this.capacity = capacity;
        this.storage = new HashMap<>();
        this.evictionPolicy = evictionPolicy;
    }

    @Override
    public void put(K key, V value) {
        if (storage.size() >= capacity) {
            K evictedKey = evictionPolicy.evict();
            if (evictedKey != null) {
                storage.remove(evictedKey);
            }
        }
        storage.put(key, value);
        evictionPolicy.onAccess(key);
    }

    @Override
    public V get(K key) {
        if (!storage.containsKey(key)) {
            return null;
        }
        evictionPolicy.onAccess(key);
        return storage.get(key);
    }

    @Override
    public boolean isFull() {
        return storage.size() >= capacity;
    }

    @Override
    public Map<K, V> getAll() {
        return new LinkedHashMap<>(storage); // or cacheMap, depending on design
    }
}
