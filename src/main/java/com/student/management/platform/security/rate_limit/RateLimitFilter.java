package com.student.management.platform.security.rate_limit;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.Duration;
import java.time.Instant;

class RateLimitFilter implements HandlerInterceptor {

  private final String targetMethod;
  private final Duration duration;
  private final int requests;

  RateLimitFilter(int requests, Duration duration, String targetMethod) {
    this.targetMethod = targetMethod;
    this.duration = duration;
    this.requests = requests;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    if (request.getMethod().equalsIgnoreCase("POST")
        || request.getMethod().equalsIgnoreCase("DELETE")) {
      return true;
    }
    HttpSession session = request.getSession();
    String requestMethod = request.getMethod();
    SessionRateHolder testSessionBucket;
    Instant now = Instant.now();
    ApiMethod apiMethod;
    if (request.getRequestURL().toString().contains("/students")) {
      testSessionBucket = (SessionRateHolder) session.getAttribute("sessionBucketStudents");
      apiMethod = ApiMethod.GET_STUDENTS;
    } else {
      testSessionBucket = (SessionRateHolder) session.getAttribute("sessionBucketMasters");
      apiMethod = ApiMethod.GET_MASTERS;
    }
    return validateSessionBucket(response, session, requestMethod, testSessionBucket, now, apiMethod);
  }

  private boolean validateSessionBucket(HttpServletResponse response, HttpSession session,
      String requestMethod, SessionRateHolder testSessionBucket,
      Instant now, ApiMethod apiMethod) {
    String sessionBucketForMethod = validateCurrentBucket(session, now, testSessionBucket, apiMethod);
    if (validateTokenConsumption(session, requestMethod, sessionBucketForMethod))
      return true;
    response.setStatus(429);
    return false;
  }

  private String validateCurrentBucket(HttpSession session, Instant now,
      SessionRateHolder testSessionBucket, ApiMethod apiMethod) {
    String sessionBucketForMethod =
        apiMethod.equals(ApiMethod.GET_MASTERS) ? "sessionBucketMasters" : "sessionBucketStudents";
    if (testSessionBucket == null || Duration.between(testSessionBucket.getFirstRequest(), now).compareTo(duration) >= 0) {
      session.setAttribute(
          sessionBucketForMethod,
          new SessionRateHolder(now, requests));
    }
    return sessionBucketForMethod;
  }

  private boolean validateTokenConsumption(HttpSession session, String requestMethod, String sessionBucketForMethod) {
    SessionRateHolder sessionRateHolder =
        (SessionRateHolder) session.getAttribute(sessionBucketForMethod);
    return requestMethod.equalsIgnoreCase(targetMethod) && checkTokenPresentAndRemoveOne(sessionRateHolder);
  }

  private boolean checkTokenPresentAndRemoveOne(SessionRateHolder sessionRateHolder) {
    Integer tokens = sessionRateHolder.getTokens();
    if (tokens > 0) {
      sessionRateHolder.setTokens(tokens - 1);
      return true;
    }
    return false;
  }

}
