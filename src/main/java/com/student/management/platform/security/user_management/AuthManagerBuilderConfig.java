package com.student.management.platform.security.user_management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

@Configuration
class AuthManagerBuilderConfig {


  @Autowired
  protected void configureAuthManagerBuilder(
      AuthenticationManagerBuilder authenticationManagerBuilder,
      DaoAuthProviderConfig daoAuthProviderConfig) {
    authenticationManagerBuilder.authenticationProvider(daoAuthProviderConfig.authenticationProvider());
  }
}
