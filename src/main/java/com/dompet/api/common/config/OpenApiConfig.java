package com.dompet.api.common.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
  @Bean
  public OpenAPI dompetOpenAPI() {
    return new OpenAPI()
      .info(new Info()
        .title("DomPet API")
        .version("v0.0.1-SNAPSHOT")
        .description("API do e-commerce pet (produtos, categorias, etc.)."))
      .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
      .components(new Components().addSecuritySchemes(
        "bearerAuth",
        new SecurityScheme()
          .name("bearerAuth")
          .type(SecurityScheme.Type.HTTP)
          .scheme("bearer")
          .bearerFormat("JWT")
      ));
  }
}
