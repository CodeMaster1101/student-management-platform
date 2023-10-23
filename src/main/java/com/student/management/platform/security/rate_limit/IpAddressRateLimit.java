package com.student.management.platform.security.rate_limit;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;

class IpAddressRateLimit implements HandlerInterceptor {

  private final CacheManager cacheManager;
  private final int maxRequests;
  private final Duration window;

  public IpAddressRateLimit(CacheManager cacheManager, int maxRequests, Duration window) {
    this.cacheManager = cacheManager;
    this.maxRequests = maxRequests;
    this.window = window;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String ipAddress = request.getRemoteAddr();
    Cache cache = cacheManager.getCache("ip-addresses");
    IpAddressInformation ipAddressInformation;
    ipAddressInformation = cache.get(ipAddress, IpAddressInformation.class);
    return validateIpAddress(response, ipAddress, cache, ipAddressInformation);
  }

  private boolean validateIpAddress(HttpServletResponse response, String ipAddress, Cache cache, IpAddressInformation ipAddressInformation) {
    if (ipAddressInformation == null) {
      return createNewIpAddressInput(ipAddress, cache);
    }
    if (ipAddressInformation.isBlocked()) {
      return tooManyRequestsResponse(response);
    }
    if (ipAddressExpired(ipAddressInformation, cache, ipAddress)) {
      return blockIpAddress(response, ipAddress, cache, ipAddressInformation);
    }
    return updateIpAddress(ipAddress, cache, ipAddressInformation);
  }

  private boolean updateIpAddress(String ipAddress, Cache cache, IpAddressInformation ipAddressInformation) {
    ipAddressInformation.setRequestCount(ipAddressInformation.getRequestCount() + 1);
    cache.put(ipAddress, ipAddressInformation);
    return true;
  }

  private boolean blockIpAddress(HttpServletResponse response, String ipAddress, Cache cache, IpAddressInformation ipAddressInformation) {
    ipAddressInformation.setBlocked(true);
    cache.put(ipAddress, ipAddressInformation);
    return tooManyRequestsResponse(response);
  }

  private boolean ipAddressExpired(IpAddressInformation ipAddressInformation, Cache cache, String ipAddress) {
    if (ipAddressInformation.getRequestCount() >= maxRequests) {
      if (Duration.between(ipAddressInformation.getFirstRequest(), Instant.now()).compareTo(window) < 0) {
          return true;
      } else {
        ipAddressInformation.setRequestCount(0);
        ipAddressInformation.setFirstRequest(Instant.now());
        cache.put(ipAddress, ipAddressInformation);
        return false;
      }
    } return false;
  }

  private boolean tooManyRequestsResponse(HttpServletResponse response) {
    response.setStatus(429);
    return false;
  }

  private boolean createNewIpAddressInput(String ipAddress, Cache cache) {
    IpAddressInformation ipAddressInformation = new IpAddressInformation(Instant.now(), 0, false);
    cache.put(ipAddress, ipAddressInformation);
    return true;
  }
}
