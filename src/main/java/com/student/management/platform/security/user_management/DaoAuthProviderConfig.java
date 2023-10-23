package com.student.management.platform.security.user_management;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
class DaoAuthProviderConfig {

  private final CustomUserDetailService userDetailService;

  DaoAuthProviderConfig(CustomUserDetailService userDetailService) {
    this.userDetailService = userDetailService;
  }

  @Bean
  BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
    auth.setUserDetailsService(userDetailService);
    auth.setPasswordEncoder(passwordEncoder());
    return auth;
  }
}
