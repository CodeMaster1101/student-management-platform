package com.student.management.platform.security.rate_limit;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@Configuration
class HttpRequestInterceptorConfig implements WebMvcConfigurer {

  private final CacheManager cacheManager;
  private static final String BASE_API_URL = "/api/v1";

  HttpRequestInterceptorConfig(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @Bean
  RateLimitFilter rateLimitFilterHttpGet() {
    int requests = 25;
    Duration duration = Duration.ofMinutes(1);
    return new RateLimitFilter(requests, duration, "GET");
  }

  @Bean
  IpAddressRateLimit rateLimitFilterForIp() {
    int maxRequests = 20;
    Duration window = Duration.ofSeconds(10);
    return new IpAddressRateLimit(cacheManager, maxRequests, window);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(rateLimitFilterForIp()).addPathPatterns("/**");
    registry.addInterceptor(rateLimitFilterHttpGet())
        .addPathPatterns(BASE_API_URL + "/masters")
        .addPathPatterns(BASE_API_URL + "/students");
  }
}
