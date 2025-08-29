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

/**
 * Filtro que autentica requisições com base no JWT enviado no header Authorization.
 * Passos:
 * 1) Extrai o token (Bearer ...)
 * 2) Valida assinatura/expiração; lê subject (email) e claim "ver" (tokenVersion)
 * 3) Carrega o usuário e compara tokenVersion do banco com a claim (suporta logout-all)
 * 4) Popula o SecurityContext com as authorities do usuário
 */
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

    // 1) Extrai o token do header Authorization
    var token = tokenService.resolveToken(request);
    if (token == null) { chain.doFilter(request, response); return; }

    try {
      // 2) Valida assinatura/expiração e obtém claims
      var claims = tokenService.parseClaims(token);
      var email = claims.getSubject();
      Integer verToken = claims.get("ver", Integer.class);

      // 3) Confere se o usuário existe e se a versão do token bate (logout-all)
      var user = usuariosRepo.findByEmail(email).orElse(null);
      if (user == null || !user.getTokenVersion().equals(verToken)) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value()); // token antigo/inválido
        return;
      }

      // 4) Cria a autenticação com as authorities (roles) e injeta no contexto
      var auth = new UsernamePasswordAuthenticationToken(
          email, null, user.getRole().getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(auth);

      chain.doFilter(request, response);
    } catch (JwtException e) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
  }
}
