package com.student.management.platform.security.main_config;

import org.springframework.security.core.AuthenticationException;

class JwtAuthenticationException extends AuthenticationException {
  JwtAuthenticationException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
