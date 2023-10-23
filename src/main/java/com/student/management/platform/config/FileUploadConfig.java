package com.student.management.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@Configuration
class FileUploadConfig {

  @Bean
  MultipartResolver multipartResolver() {
    return new StandardServletMultipartResolver();
  }
}
