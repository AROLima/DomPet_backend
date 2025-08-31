package com.dompet.api.common.config;

import com.dompet.api.features.auth.security.JwtAuthFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
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
 * - Sessão stateless (JWT)
 * - CORS liberando Authorization e Content-Type (ajuste origins em produção)
 * - Libera públicos: /auth/login, /auth/register, swagger, h2-console e GET de /produtos/**
 * - Exige autenticação em: /auth/logout, /auth/logout-all, /cart/**, /carrinho/**, /pedidos/**
 * - Restringe ADMIN para mutações de /produtos/** e PATCH de /pedidos/**
 * - Injeta filtro JWT antes do UsernamePasswordAuthenticationFilter
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

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
        // públicos
        .requestMatchers("/auth/login", "/auth/register").permitAll()
        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
        .requestMatchers("/h2-console/**").permitAll()
        // leitura pública de produtos
        .requestMatchers(HttpMethod.GET, "/produtos/**").permitAll()
        // precisa estar logado
        .requestMatchers("/auth/logout", "/auth/logout-all").authenticated()
        // ADMIN para atualizar status de pedidos (coloque antes do matcher genérico de /pedidos/**)
        .requestMatchers(HttpMethod.PATCH, "/pedidos/**").hasRole("ADMIN")
        // ADMIN-only para mutações de produtos
        .requestMatchers(HttpMethod.POST,   "/produtos/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.PUT,    "/produtos/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.PATCH,  "/produtos/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.DELETE, "/produtos/**").hasRole("ADMIN")
        // Genérico autenticado
        .requestMatchers("/cart/**", "/carrinho/**", "/pedidos/**").authenticated()
        // demais
        .anyRequest().authenticated()
      )
      .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  /**
   * CORS permissivo para desenvolvimento. Em produção, restrinja os domínios.
   */
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cfg = new CorsConfiguration();
    cfg.addAllowedOriginPattern("*"); // TODO: restringir em produção
    cfg.addAllowedHeader("Authorization");
    cfg.addAllowedHeader("Content-Type");
    cfg.addAllowedMethod("GET");
    cfg.addAllowedMethod("POST");
    cfg.addAllowedMethod("PUT");
    cfg.addAllowedMethod("PATCH");
    cfg.addAllowedMethod("DELETE");
    cfg.addAllowedMethod("OPTIONS");
  // Permitir que o front leia cabeçalhos úteis nas respostas (Flutter Web/browser)
  cfg.addExposedHeader("ETag");
  cfg.addExposedHeader("Location");
  cfg.addExposedHeader("X-API-Version");
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cfg);
    return source;
  }
}
