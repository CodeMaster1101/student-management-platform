package com.student.management.platform.security.rate_limit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
class ScheduledIpAddressService {

  private final CacheManager cacheManager;

  Logger logger = LoggerFactory.getLogger(ScheduledIpAddressService.class);

  ScheduledIpAddressService(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @Scheduled(fixedRate = 45 * 60 * 1000)
  public void unbanIpAddresses() {
    Cache cache = cacheManager.getCache("ip-addresses");
    assert cache != null;
    cache.clear();
    String message = "Cleared cache for ip addresses";
    logger.info(message);
  }

}
