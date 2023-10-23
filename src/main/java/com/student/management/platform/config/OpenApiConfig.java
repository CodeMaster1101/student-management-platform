
package com.student.management.platform.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OpenApiConfig {

  @Bean
  GroupedOpenApi api() {
    return GroupedOpenApi.builder()
        .group("code.master-service")
        .pathsToMatch("/**")
        .build();
  }

  @Bean
  OpenAPI apiInfo() {
    return new OpenAPI().info(new Info().title("Code Master service - REST API")
        .version("0.0.1")
        .termsOfService("")
        .license(new License().name("self-certified-license")
            .url("")));
  }
}
