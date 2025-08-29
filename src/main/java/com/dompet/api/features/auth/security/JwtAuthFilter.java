package com.dompet.api.features.auth.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dompet.api.features.auth.token.TokenService;
import com.dompet.api.features.usuarios.repo.UsuariosRepository;

import io.jsonwebtoken.JwtException;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private final TokenService tokenService;
  private final UsuariosRepository usuariosRepo;

  public JwtAuthFilter(TokenService tokenService, UsuariosRepository usuariosRepo) {
    this.tokenService = tokenService;
    this.usuariosRepo = usuariosRepo;
  }

  @Override
  @NonNull
  protected void doFilterInternal(
      @org.springframework.lang.NonNull HttpServletRequest request,
      @org.springframework.lang.NonNull HttpServletResponse response,
      @org.springframework.lang.NonNull FilterChain chain)
      throws ServletException, IOException {

    var token = tokenService.resolveToken(request);
    if (token == null) { chain.doFilter(request, response); return; }

    try {
      var claims = tokenService.parseClaims(token); // valida assinatura/exp
      var email = claims.getSubject();
      Integer verToken = claims.get("ver", Integer.class);

      var user = usuariosRepo.findByEmail(email).orElse(null);
      if (user == null || !user.getTokenVersion().equals(verToken)) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value()); // token antigo/inválido
        return;
      }

      var auth = new UsernamePasswordAuthenticationToken(
          email, null, user.getRole().getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(auth);

      chain.doFilter(request, response);
    } catch (JwtException e) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
  }
}
