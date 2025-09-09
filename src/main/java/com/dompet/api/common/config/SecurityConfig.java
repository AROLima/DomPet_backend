package com.dompet.api.common.config;

import com.dompet.api.features.auth.security.JwtAuthFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configurações de segurança da aplicação.
 *
 * Bloco metodológico (o que este arquivo entrega):
 * - Configura o pipeline de segurança do Spring para trabalhar com JWT.
 * - Define quais rotas são públicas e quais exigem autenticação/roles.
 * - Fornece beans compartilhados: PasswordEncoder, AuthenticationManager e CORS.
 *
 * Observações didáticas (linha-a-linha nos pontos críticos):
 * - A aplicação usa sessão stateless: cada requisição traz o JWT no header Authorization.
 * - O JWT é validado por um filtro (JwtAuthFilter) que popula o SecurityContext.
 * - CORS é permissivo aqui para facilitar desenvolvimento com front-end local.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Value("${app.cors.allowed-origins:}")
  private String allowedOriginsProp;

  @Bean
  /**
   * Encoder de senhas com BCrypt (padrão de mercado).
   */
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  /**
   * Expõe o AuthenticationManager configurado pelo Spring Security.
   */
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  /**
   * Define a cadeia de filtros e as regras de autorização.
   */
  public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
    http
      // API com JWT não usa CSRF
      .csrf(csrf -> csrf.disable())
      // Habilita CORS (config detalhada no bean abaixo)
      .cors(cors -> {})
      // Sem sessão de servidor: cada request porta o JWT
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      // Permite o H2 console em <iframe> da mesma origem
      .headers(h -> h.frameOptions(f -> f.sameOrigin()))
      .authorizeHttpRequests(auth -> auth
        // ENDPOINTS PÚBLICOS (não exigem autenticação)
        .requestMatchers("/auth/login", "/auth/register").permitAll()
        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
        .requestMatchers("/h2-console/**").permitAll()
        // Leitura pública de produtos: GET /produtos/**
        .requestMatchers(HttpMethod.GET, "/produtos/**").permitAll()
        // Logout e logout-all requerem autenticação
        .requestMatchers("/auth/logout", "/auth/logout-all").authenticated()
        // Regras de ROLE: administradores podem alterar pedidos e mutar produtos
        .requestMatchers(HttpMethod.PATCH, "/pedidos/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.POST,   "/produtos/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.PUT,    "/produtos/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.PATCH,  "/produtos/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.DELETE, "/produtos/**").hasRole("ADMIN")
        // Rotas de negócio que exigem autenticação (carrinho e pedidos)
        .requestMatchers("/cart/**", "/carrinho/**", "/pedidos/**").authenticated()
        // Demais requisições também exigem autenticação por padrão
        .anyRequest().authenticated()
      )
      // Adiciona o filtro JWT antes do filtro padrão de autenticação por username/senha
      .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  /**
   * CORS permissivo para desenvolvimento. Em produção, restrinja os domínios.
   */
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cfg = new CorsConfiguration();

    // If property provided, split by comma; else allow all (dev fallback)
    if (allowedOriginsProp != null && !allowedOriginsProp.isBlank()) {
      for (String origin : allowedOriginsProp.split(",")) {
        String trimmed = origin.trim();
        if (!trimmed.isEmpty()) cfg.addAllowedOrigin(trimmed);
      }
    } else {
      cfg.addAllowedOriginPattern("*");
    }

    cfg.addAllowedHeader("*");
    cfg.addAllowedMethod("GET");
    cfg.addAllowedMethod("POST");
    cfg.addAllowedMethod("PUT");
    cfg.addAllowedMethod("PATCH");
    cfg.addAllowedMethod("DELETE");
    cfg.addAllowedMethod("OPTIONS");
    cfg.addExposedHeader("ETag");
    cfg.addExposedHeader("Location");
    cfg.addExposedHeader("X-API-Version");
    cfg.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cfg);
    return source;
  }
}
// Atenção: revisar CORS e políticas de header em produção; configuração atual é permissiva para desenvolvimento.
