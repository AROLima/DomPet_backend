package com.dompet.api.common.config;

// ATENÇÃO: Flutter (mobile) não precisa de CORS. Esta classe é um exemplo comentado para Flutter Web.
// Para ativar, remova os comentários e ajuste os domínios permitidos.
// NOTA: CORS já está configurado no SecurityConfig.java

/*
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cfg = new CorsConfiguration();
    cfg.addAllowedOrigin("http://localhost:4200");
    cfg.addAllowedOrigin("https://app.dompet.com");
    cfg.addAllowedHeader("Authorization");
    cfg.addAllowedHeader("Content-Type");
    cfg.addAllowedMethod("GET");
    cfg.addAllowedMethod("POST");
    cfg.addAllowedMethod("PUT");
    cfg.addAllowedMethod("PATCH");
    cfg.addAllowedMethod("DELETE");
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cfg);
    return source;
  }
}
*/

