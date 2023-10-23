package com.student.management.platform.security.main_config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletResponse;

@Configuration
class SecurityConfig implements WebMvcConfigurer {


  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenProvider jwtTokenProvider) throws Exception {
    JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider);
    http
        .cors()
        .and()
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .authorizeRequests()
        .mvcMatchers(HttpMethod.GET, "/api/v1/**").permitAll()
        .mvcMatchers(HttpMethod.POST, "/api/v1/security/**").permitAll()
        .mvcMatchers("/api/v1/management/**").hasAuthority("admin")
        .mvcMatchers(HttpMethod.DELETE, "/api/v1/masters").hasAuthority("admin")
        .mvcMatchers(HttpMethod.POST, "/api/v1/masters").hasAuthority("admin")
        .mvcMatchers(HttpMethod.DELETE, "/api/v1/students").hasAuthority("admin")
        .mvcMatchers(HttpMethod.POST, "/api/v1/students").hasAuthority("admin")
        .anyRequest().authenticated()
        .and()
        .exceptionHandling()
        .authenticationEntryPoint((request, response, authException)
            -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
        .accessDeniedHandler((request, response, accessDeniedException)
            -> response.sendError(HttpServletResponse.SC_FORBIDDEN))
        .and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .csrf().disable();
    return http.build();
  }

}
