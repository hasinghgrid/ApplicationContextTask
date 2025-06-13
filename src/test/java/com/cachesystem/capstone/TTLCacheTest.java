package com.cachesystem.capstone;

import com.cachesystem.capstone.eviction.TTLCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TTLCacheTest {

    private TTLCache<String, String> ttlCache;

    @BeforeEach
    void setUp() {
        ttlCache = new TTLCache<>(2000); // TTL = 2 seconds
    }

    @Test
    void testOnAccessStoresTimestamp() {
        ttlCache.onAccess("key1");
        assertFalse(ttlCache.isExpired("key1"));
    }

    @Test
    void testIsExpiredReturnsTrueAfterTTL() throws InterruptedException {
        ttlCache.onAccess("key1");
        TimeUnit.MILLISECONDS.sleep(2500);
        assertTrue(ttlCache.isExpired("key1"));
    }

    @Test
    void testClearExpiredCacheRemovesExpiredItems() throws InterruptedException {
        ttlCache.onAccess("1");
        ttlCache.onAccess("2");
        TimeUnit.MILLISECONDS.sleep(2500);
        ttlCache.clearExpiredCache();
        assertTrue(ttlCache.isExpired("1"));
        assertTrue(ttlCache.isExpired("2"));
        assertNull(ttlCache.evict());
    }

    @Test
    void testEvictReturnsNullIfNoExpiredEntries() {
        ttlCache.onAccess("1");
        ttlCache.onAccess("2");
        assertNull(ttlCache.evict());
    }
}
