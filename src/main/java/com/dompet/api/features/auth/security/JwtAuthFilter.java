// JwtAuthFilter.java
// Filtro de autenticação JWT. Processo:
// 1) extrai token do Authorization header
// 2) valida assinatura e expiração via TokenService
// 3) compara tokenVersion (claim 'ver') com valor no banco para suportar logout-all
// 4) cria Authentication no SecurityContextHolder com as authorities do usuário
package com.dompet.api.features.auth.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dompet.api.features.auth.token.TokenService;
import com.dompet.api.features.usuarios.repo.UsuariosRepository;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.ExpiredJwtException;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro que autentica requisições com base no JWT enviado no header Authorization.
 *
 * Explicação passo a passo (útil para estudo):
 * 1) Extrai o token (Authorization: Bearer <token>)
 * 2) Valida assinatura e expiração via {@link com.dompet.api.features.auth.token.TokenService}
 * 3) Lê subject (email) e claim "ver" (tokenVersion) das claims
 * 4) Busca o usuário no banco e compara tokenVersion (suporta logout-all)
 * 5) Se válido, cria um UsernamePasswordAuthenticationToken com as authorities e popula o SecurityContext
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private final TokenService tokenService;
  private final UsuariosRepository usuariosRepo;
  @Value("${app.jwt.refresh-grace-seconds:30}")
  private long refreshGraceSeconds; // tempo máximo em segundos após expiração para ainda aceitar /auth/refresh
  private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

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
  // Linha: comparar a claim 'ver' com o tokenVersion do banco permite invalidar
  // tokens emitidos antes de um "logout-all" (bumpTokenVersion).
  var user = usuariosRepo.findByEmail(email).orElse(null);
      if (user == null || !user.getTokenVersion().equals(verToken)) {
        log.warn("JWT inválido: {} (user encontrado? {} | verToken={}, verDb={})", email,
            user != null, verToken, user != null ? user.getTokenVersion() : null);
        response.setStatus(HttpStatus.UNAUTHORIZED.value()); // token antigo/inválido
        return;
      }

      // 4) Cria a autenticação com as authorities (roles) e injeta no contexto
      var auth = new UsernamePasswordAuthenticationToken(
          email, null, user.getRole().getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(auth);

      chain.doFilter(request, response);
    } catch (ExpiredJwtException ex) {
      String uri = request.getRequestURI();
      long secondsSinceExpiration = (System.currentTimeMillis() - ex.getClaims().getExpiration().getTime()) / 1000L;
      boolean isRefresh = "/auth/refresh".equals(uri);

      // Evita múltiplos logs/execuções para o mesmo request (por encadeamento de filtros)
      if (request.getAttribute("_expiredHandled") != null) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return;
      }
      request.setAttribute("_expiredHandled", Boolean.TRUE);

      if (isRefresh && secondsSinceExpiration <= refreshGraceSeconds) {
        var claims = ex.getClaims();
        var email = claims.getSubject();
        Integer verToken = claims.get("ver", Integer.class);
        var user = usuariosRepo.findByEmail(email).orElse(null);
        if (user != null && user.getTokenVersion().equals(verToken)) {
          log.debug("Aceitando token expirado ({}s) dentro do grace {}s para refresh de {}", secondsSinceExpiration, refreshGraceSeconds, email);
          var auth = new UsernamePasswordAuthenticationToken(
              email, null, user.getRole().getAuthorities());
          SecurityContextHolder.getContext().setAuthentication(auth);
          chain.doFilter(request, response);
          return;
        }
      }
  // Fora do grace ou refresh inválido: apenas DEBUG para não poluir logs em produção
  log.debug("JWT expirado rejeitado ({}s > {}s) uri={}", secondsSinceExpiration, refreshGraceSeconds, uri);
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
    } catch (JwtException e) {
      log.warn("Falha ao validar JWT: {}", e.getMessage());
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
  }
}
// Nota: este filtro é crítico para a segurança stateless; erros aqui impedem acesso ao restante da aplicação.
