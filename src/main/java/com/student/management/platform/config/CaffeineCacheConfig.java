package com.student.management.platform.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
class CaffeineCacheConfig {

  @Bean
  CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager();
    cacheManager.setCaffeine(cacheProperties());
    return cacheManager;
  }

  Caffeine<Object, Object> cacheProperties() {
    return Caffeine.newBuilder()
        .initialCapacity(100)
        .maximumSize(500)
        .expireAfterAccess(1000, TimeUnit.DAYS);
  }
}
