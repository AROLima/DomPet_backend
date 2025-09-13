// TokenService: cria e valida JWTs (HS256)
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
/**
 * Serviço responsável por criar e validar tokens JWT (HS256).
 *
 * Notas para estudo:
 * - O segredo (`app.jwt.secret`) deve ter pelo menos 32 bytes; recomenda-se fornecer em Base64.
 * - Em ambiente de DEV, se o segredo não estiver definido, geramos uma chave volátil para permitir testes.
 * - Incluímos a claim "ver" com o tokenVersion do usuário para permitir invalidar tokens após logout-all.
 */
public class TokenService {

  private static final Logger log = LoggerFactory.getLogger(TokenService.class);
  private final SecretKey key; // chave usada para assinar tokens HS256
  private final long expirationMs; // validade em milissegundos
  private final UsuariosRepository usuariosRepo; // para ler tokenVersion do usuário

  /**
   * Construtor:
   * - `secret` é lido da configuração (recomendado Base64). Se vazio, gera chave volátil para DEV.
   * - `expirationMs` define quanto tempo o token é válido.
   */
  public TokenService(
  @Value("${app.jwt.secret:}") String secret,
  @Value("${app.jwt.expiration-ms:3600000}") long expirationMs,
      UsuariosRepository usuariosRepo) {

  /**
   * Resumo didático (o que olhar ao estudar este arquivo):
   * - Gere tokens com claims sem expor segredos nos códigos-fonte.
   * - Observe o uso de `ver` (tokenVersion) como mecanismo simples de invalidar todos os tokens antigos.
   * - parseClaims() lança JwtException para sinalizar token inválido/expirado.
   */
    byte[] raw;
    if (secret == null || secret.isBlank()) {
      // Gera chave volátil para DEV (não usar em produção)
      raw = new byte[64];
      java.security.SecureRandom rand = new java.security.SecureRandom();
      rand.nextBytes(raw);
      log.warn("APP_JWT_SECRET não definido. Gerando chave HS256 volátil para DEV. Defina APP_JWT_SECRET para estabilidade dos tokens.");
    } else {
      try { // tenta decodificar Base64 primeiro
        raw = Base64.getDecoder().decode(secret);
      } catch (IllegalArgumentException ex) {
        // Se não for Base64, usa bytes UTF-8 do valor
        raw = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
      }
    }
    if (raw.length < 32) {
      throw new IllegalArgumentException("app.jwt.secret precisa ter >= 32 bytes (HS256). Gere em Base64.");
    }
    this.key = Keys.hmacShaKeyFor(raw);
    this.expirationMs = expirationMs;
    this.usuariosRepo = usuariosRepo;
  }

  /** Expiração do token em milissegundos (para clientes móveis/web). */
  public long getExpirationMs() {
    return expirationMs;
  }

  /**
   * Gera JWT com claims úteis:
   * - sub = email
   * - roles = lista de authorities
   * - ver = tokenVersion do usuário (usado para invalidar tokens antigos)
   */
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
        .claim("ver", user.getTokenVersion())   // versão para suportar logout-all
        .setIssuedAt(now)
        .setExpiration(exp)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * Extrai token do header Authorization (Bearer <token>).
   * Retorna null se não houver token.
   */
  public String resolveToken(HttpServletRequest req) {
    var h = req.getHeader("Authorization");
    if (h != null && h.startsWith("Bearer ")) return h.substring(7);
    return null;
  }

  /**
   * Valida assinatura e expiração; retorna Claims se válido.
   * Lança JwtException quando inválido/expirado.
   */
  public Claims parseClaims(String token) throws JwtException {
    return Jwts.parserBuilder().setSigningKey(key).build()
        .parseClaimsJws(token).getBody();
  }

  /** Atalho: obtém o subject (email) do token. */
  public String getSubject(String token) {
    return parseClaims(token).getSubject();
  }

  /** True se o token for sintaticamente válido e não expirado. */
  public boolean isValid(String token) {
    try {
      parseClaims(token);
      return true;
    } catch (JwtException e) {
      return false;
    }
  }
}
