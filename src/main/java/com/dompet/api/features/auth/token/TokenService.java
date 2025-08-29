// com/dompet/api/features/auth/token/TokenService.java
package com.dompet.api.features.auth.token;

import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.dompet.api.features.usuarios.repo.UsuariosRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import javax.crypto.SecretKey;

@Service
// Serviço que cria e valida tokens JWT (HS256) + versão de sessão (logout-all)
public class TokenService {

  private final SecretKey key;
  private final long expirationMs;
  private final UsuariosRepository usuariosRepo;

  public TokenService(
      @Value("${app.jwt.secret}") String secret,                 // Base64 recomendado
      @Value("${app.jwt.expiration-ms}") long expirationMs,
      UsuariosRepository usuariosRepo) {

    byte[] raw;
    try { // tenta Base64 primeiro
      raw = Base64.getDecoder().decode(secret);
    } catch (IllegalArgumentException ex) {
      raw = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
    if (raw.length < 32) {
      throw new IllegalArgumentException("app.jwt.secret precisa ter >= 32 bytes (HS256). Gere em Base64.");
    }
    this.key = Keys.hmacShaKeyFor(raw);
    this.expirationMs = expirationMs;
    this.usuariosRepo = usuariosRepo;
  }

  /** Gera JWT com: sub=email, roles, ver=tokenVersion do usuário */
  public String generate(UserDetails principal) {
    var email = principal.getUsername();
    var user  = usuariosRepo.findByEmail(email).orElseThrow();

    var roles = principal.getAuthorities().stream()
        .map(a -> a.getAuthority())
        .collect(Collectors.toList());

    var now = new Date();
    var exp = new Date(now.getTime() + expirationMs);

    return Jwts.builder()
        .setSubject(email)
        .claim("roles", roles)
        .claim("ver", user.getTokenVersion())   // << versão para suportar logout-all
        .setIssuedAt(now)
        .setExpiration(exp)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  /** Lê o token do header Authorization: Bearer <token> */
  public String resolveToken(HttpServletRequest req) {
    var h = req.getHeader("Authorization");
    if (h != null && h.startsWith("Bearer ")) return h.substring(7);
    return null;
  }

  /** Valida assinatura/expiração e retorna Claims (lança JwtException se inválido) */
  public Claims parseClaims(String token) throws JwtException {
    return Jwts.parserBuilder().setSigningKey(key).build()
        .parseClaimsJws(token).getBody();
  }

  /** Atalho: subject (email) do token */
  public String getSubject(String token) {
    return parseClaims(token).getSubject();
  }

  /** True se sintaticamente válido e não expirado */
  public boolean isValid(String token) {
    try {
      parseClaims(token);
      return true;
    } catch (JwtException e) {
      return false;
    }
  }
}
