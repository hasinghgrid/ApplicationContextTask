package com.cachesystem.capstone.eviction;

public interface EvictionPolicy<K> {
    void onAccess(K key);
    K evict();
}
