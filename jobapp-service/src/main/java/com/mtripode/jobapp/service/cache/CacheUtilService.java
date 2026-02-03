package com.mtripode.jobapp.service.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

/**
 * Generic utility service for managing Spring caches programmatically. Allows
 * clearing entire caches, evicting specific entries, and putting/getting
 * values.
 */
@Service
public class CacheUtilService {

    private final CacheManager cacheManager;

    public CacheUtilService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Clear all entries from a given cache.
     *
     * @param cacheName the name of the cache to clear
     */
    public void clearCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    /**
     * Evict a single entry from a given cache.
     *
     * @param cacheName the name of the cache
     * @param key the key of the entry to evict
     */
    public void evictCacheEntry(String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

    /**
     * Put or update a value in the cache manually.
     *
     * @param cacheName the name of the cache
     * @param key the key of the entry
     * @param value the value to store
     */
    public void putCacheEntry(String cacheName, Object key, Object value) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.put(key, value);
        }
    }

    /**
     * Retrieve a value from the cache manually.
     *
     * @param cacheName the name of the cache
     * @param key the key of the entry
     * @param type the expected type of the value
     * @param <T> generic type parameter
     * @return the cached value or null if not present
     */
    public <T> T getCacheEntry(String cacheName, Object key, Class<T> type) {
        Cache cache = cacheManager.getCache(cacheName);
        return cache != null ? cache.get(key, type) : null;
    }
}
