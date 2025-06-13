package com.cachesystem.capstone.cache;

import java.util.Map;

/**
 * Generic Cache Interface defining basic cache operations.
 */
public interface Cache<K, V> {
    void put(K key, V value);
    V get(K key);
    boolean isFull();

    Map<K, V> getAll();
}
