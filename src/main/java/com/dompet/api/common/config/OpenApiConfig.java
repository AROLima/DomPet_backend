package com.dompet.api.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do springdoc-openapi.
 * - Define o esquema de segurança bearerAuth (JWT)
 * - Informa metadados básicos da API
 */
@Configuration
@OpenAPIDefinition(
  info = @Info(title = "DomPet API", version = "1.0", description = "API da DomPet para apps Flutter"),
  servers = { @Server(url = "/", description = "Servidor local") }
)
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {

  @Bean
  public OpenAPI dompetOpenAPI() {
  return new OpenAPI()
    .info(new io.swagger.v3.oas.models.info.Info()
      .title("DomPet API")
      .version("1.0")
      .description("API do e-commerce pet (produtos, carrinho, pedidos, autenticação).")
      .contact(new Contact().name("DomPet").url("https://dompet.example"))
      .license(new License().name("Apache-2.0")))
    .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
    .components(new Components().addSecuritySchemes(
      "bearerAuth",
      new io.swagger.v3.oas.models.security.SecurityScheme()
        .name("bearerAuth")
        .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT")
    ));
  }
}
