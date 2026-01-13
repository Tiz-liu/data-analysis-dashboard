package com.dad.service.dashboard.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 多级缓存配置
 *
 * L1: Caffeine本地缓存（1分钟TTL）
 * L2: Redis分布式缓存（5分钟TTL）
 *
 * @author orange
 */
@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * L1: Caffeine本地缓存
     * TTL: 1分钟
     * 容量: 1000个图表
     * 作用: 快速响应，减少Redis访问
     */
    @Bean
    public CaffeineCacheManager caffeineCacheManager() {
        log.info("Initializing Caffeine local cache (L1)...");
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("chartData");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)  // 最多缓存1000个图表
                .expireAfterWrite(1, TimeUnit.MINUTES)  // L1: 1分钟TTL
                .recordStats()  // 记录缓存统计信息
                .removalListener((key, value, cause) -> {
                    log.debug("Caffeine cache removed: key={}, cause={}", key, cause);
                })
        );
        log.info("Caffeine local cache initialized successfully");
        return cacheManager;
    }

    /**
     * L2: Redis分布式缓存
     * TTL: 5分钟
     * 作用: 跨服务实例共享缓存，持久化存储
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory factory) {
        log.info("Initializing Redis distributed cache (L2)...");

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))  // L2: 5分钟TTL
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues()  // 不缓存null值
                .computePrefixWith(cacheName -> "dad:cache:" + cacheName + ":");  // 添加前缀

        RedisCacheManager cacheManager = RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .transactionAware()  // 支持事务
                .build();

        log.info("Redis distributed cache initialized successfully");
        return cacheManager;
    }

    /**
     * 组合缓存管理器（多级缓存）
     * 查找顺序: L1 (Caffeine) → L2 (Redis)
     *
     * @param caffeineCacheManager 本地缓存管理器
     * @param redisCacheManager    Redis缓存管理器
     * @return 组合缓存管理器
     */
    @Bean
    @Primary
    public CacheManager compositeCacheManager(
            CaffeineCacheManager caffeineCacheManager,
            RedisCacheManager redisCacheManager) {
        log.info("Initializing composite cache manager (L1 + L2)...");

        CompositeCacheManager compositeCacheManager = new CompositeCacheManager(
                caffeineCacheManager,  // L1: 优先查找本地缓存
                redisCacheManager      // L2: 本地未命中时查找Redis
        );

        log.info("Composite cache manager initialized successfully");
        return compositeCacheManager;
    }
}
