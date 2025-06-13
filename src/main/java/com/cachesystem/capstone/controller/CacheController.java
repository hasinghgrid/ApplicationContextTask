package com.cachesystem.capstone.controller;

import java.util.Map;
import com.cachesystem.capstone.cache.Cache;
import com.cachesystem.capstone.cache.CacheFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cache")
public class CacheController {

    private final Cache<String, String> cache;

    public CacheController(
            @Value("${cache.size}") int size,
            @Value("${cache.type}") String type,
            @Value("${cache.ttl}") long ttl
    ) {
        this.cache = CacheFactory.createCache(size, type, ttl);
    }

    @PostMapping("/put")
    public String put(@RequestParam String key, @RequestParam String value) {
        cache.put(key, value);
        return "Added successfully.";
    }

    @GetMapping("/get")
    public String get(@RequestParam String key) {
        String value = cache.get(key);
        return value != null ? value : "Key not found";
    }

    @GetMapping("/all") // ✅ FIXED HERE
    public Map<String, String> getAll() {
        return cache.getAll();
    }
}
