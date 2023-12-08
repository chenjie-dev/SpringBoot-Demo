package com.chenjie.caffeine.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Value("${spring.cache.expireAfterAccess}")
    Long expireAfterAccess;

    @Value("${spring.cache.initialCapacity}")
    Integer initialCapacity;

    @Value("${spring.cache.maximumSize}")
    Long maximumSize;


    @Bean
    public Cache<String, Object> caffeineCache() {
        return Caffeine.newBuilder()
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterWrite(expireAfterAccess, TimeUnit.SECONDS)
                // 初始的缓存空间大小
                .initialCapacity(initialCapacity)
                // 缓存的最大条数
                .maximumSize(maximumSize).build();
    }

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterAccess(expireAfterAccess, TimeUnit.SECONDS)
                // 初始的缓存空间大小
                .initialCapacity(initialCapacity)
                // 缓存的最大条数
                .maximumSize(maximumSize));
        return cacheManager;
    }
}