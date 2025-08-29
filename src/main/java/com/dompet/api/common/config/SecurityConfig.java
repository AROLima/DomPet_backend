package com.dompet.api.common.config;

import com.dompet.api.features.auth.security.JwtAuthFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .headers(h -> h.frameOptions(f -> f.sameOrigin())) // H2 console
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
  .requestMatchers("/cart/**", "/pedidos/**").authenticated()
        // demais
        .anyRequest().authenticated()
      )
      .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
