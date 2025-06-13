package com.cachesystem.capstone;

import com.cachesystem.capstone.cache.Cache;
import com.cachesystem.capstone.cache.CacheFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LFUCacheTest {

    @Test
    void testCacheEvictionLFU() {
        Cache<String, String> cache = CacheFactory.createCache(2, "LFU", 0);

        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.get("key1"); // Access "key1" once
        cache.put("key3", "value3");

        assertNull(cache.get("key2")); // Should be evicted
        assertEquals("value1", cache.get("key1"));
        assertEquals("value3", cache.get("key3"));
    }
}
