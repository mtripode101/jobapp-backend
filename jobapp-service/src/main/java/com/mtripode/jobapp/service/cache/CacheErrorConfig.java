package com.mtripode.jobapp.service.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheErrorConfig {

    private static final Logger logger = LoggerFactory.getLogger(CacheErrorConfig.class);

    @Bean
    public CacheErrorHandler cacheErrorHandler() {
        return new SimpleCacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                logger.warn("Redis GET error on cache={} key={}: {}", 
                            cache != null ? cache.getName() : "unknown", key, exception.getMessage(), exception);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                logger.warn("Redis PUT error on cache={} key={}: {}", 
                            cache != null ? cache.getName() : "unknown", key, exception.getMessage(), exception);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                logger.warn("Redis EVICT error on cache={} key={}: {}", 
                            cache != null ? cache.getName() : "unknown", key, exception.getMessage(), exception);
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                logger.warn("Redis CLEAR error on cache={}: {}", 
                            cache != null ? cache.getName() : "unknown", exception.getMessage(), exception);
            }
        };
    }
}
