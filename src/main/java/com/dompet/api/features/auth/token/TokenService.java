// com/dompet/api/features/auth/token/TokenService.java
package com.dompet.api.features.auth.token;

import java.util.Date;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

@Service
public class TokenService {

  private final SecretKey key;
  private final long expirationMs;

  public TokenService(
      @Value("${app.jwt.secret}") String secret,
      @Value("${app.jwt.expiration-ms}") long expirationMs) {
    // Support both base64-encoded and raw secrets; ensure minimum size for HS256 (32 bytes)
    byte[] raw;
    try {
      raw = Base64.getDecoder().decode(secret);
    } catch (IllegalArgumentException ex) {
      raw = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
    this.key = Keys.hmacShaKeyFor(raw);
    this.expirationMs = expirationMs;
  }

  public String generate(UserDetails user) {
    var now = new Date();
    var exp = new Date(now.getTime() + expirationMs);
    return Jwts.builder()
        .setSubject(user.getUsername()) // email
        .claim("roles", user.getAuthorities().stream().map(a->a.getAuthority()).toList())
        .setIssuedAt(now)
        .setExpiration(exp)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public String getSubject(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build()
        .parseClaimsJws(token).getBody().getSubject();
  }

  public boolean isValid(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (JwtException e) {
      return false;
    }
  }
}
