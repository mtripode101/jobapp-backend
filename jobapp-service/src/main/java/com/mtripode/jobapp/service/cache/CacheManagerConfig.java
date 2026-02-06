package com.mtripode.jobapp.service.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class CacheManagerConfig {

    private static final Logger logger = LoggerFactory.getLogger(CacheManagerConfig.class);

    /**
     * Provides a CacheManager bean. Tries to use Redis as the cache backend. If
     * Redis is not available, falls back to an in-memory
     * ConcurrentMapCacheManager.
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        try {
            // Attempt to build a Redis-backed CacheManager
            return RedisCacheManager.builder(redisConnectionFactory).build();
        } catch (RedisConnectionFailureException ex) {
            // If Redis is unavailable, log the error and fall back to in-memory cache
            logger.error("Redis is unavailable, falling back to ConcurrentMapCacheManager (in-memory)", ex);
            return new ConcurrentMapCacheManager();
        }
    }

    @Bean
    @ConditionalOnBean(RedisConnectionFactory.class)
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        logger.info("Using RedisCacheManager");
        return RedisCacheManager.builder(redisConnectionFactory).build();
    }

    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    public CacheManager fallbackCacheManager() {
        logger.warn("Redis not configured, using in-memory ConcurrentMapCacheManager");
        return new ConcurrentMapCacheManager("jobs-applications");
    }

}
