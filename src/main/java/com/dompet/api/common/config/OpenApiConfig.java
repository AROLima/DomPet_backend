package com.dompet.api.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
  @Bean
  public OpenAPI dompetOpenAPI() {
    return new OpenAPI().info(new Info()
      .title("DomPet API")
      .version("v0.0.1-SNAPSHOT")
      .description("API do e-commerce pet (produtos, categorias, etc.)."));
  }
}
