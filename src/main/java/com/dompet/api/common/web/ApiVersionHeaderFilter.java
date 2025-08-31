package com.dompet.api.common.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import java.io.IOException;

/**
 * Filtro simples que injeta o header X-API-Version em todas as respostas.
 *
 * Útil para o frontend detectar mudanças de versão da API durante desenvolvimento.
 */
@Component
public class ApiVersionHeaderFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
      throws ServletException, IOException {
  // Insere cabeçalho de versão para que o front-end possa reagir a mudanças da API
  response.setHeader("X-API-Version", "dompet-1");
    filterChain.doFilter(request, response);
  }
}
