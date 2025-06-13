package com.cachesystem.capstone;

import com.cachesystem.capstone.cache.CacheFactory;
import com.cachesystem.capstone.exception.InvalidCacheTypeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CacheTest {
    @Test
    void testInvalidCacheTypeException() {
        Exception exception = assertThrows(InvalidCacheTypeException.class,
                () -> CacheFactory.createCache(2, "INVALID", 0));
        assertEquals("Invalid cache type: INVALID", exception.getMessage());
    }
}
