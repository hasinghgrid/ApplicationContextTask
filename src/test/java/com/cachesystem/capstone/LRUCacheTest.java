package com.cachesystem.capstone;

import com.cachesystem.capstone.cache.Cache;
import com.cachesystem.capstone.cache.CacheFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LRUCacheTest {

    @Test
    void testCacheEvictionLRU() {
        Cache<String, String> cache = CacheFactory.createCache(2, "LRU", 0);

        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");

        assertNull(cache.get("key1")); // LRU eviction
        assertEquals("value2", cache.get("key2"));
        assertEquals("value3", cache.get("key3"));
    }
}
