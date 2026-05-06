package com.kruosant.bookwalker.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
  private final SecretKey secretKey;
  private final long expiration;

  public JwtService(
      @Value("${app.jwt.secret}") String secret,
      @Value("${app.jwt.expiration}") long expiration
  ) {
    byte[] keyBytes = secret.length() >= 32
        ? secret.getBytes(StandardCharsets.UTF_8)
        : Decoders.BASE64.decode("Y2hhbmdlLW1lLWNoYW5nZS1tZS1jaGFuZ2UtbWUtY2hhbmdlLW1l");
    this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    this.expiration = expiration;
  }

  public String generateToken(ClientPrincipal principal) {
    return Jwts.builder()
        .subject(principal.getUsername())
        .claim("role", principal.getClient().getRole().name())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(secretKey)
        .compact();
  }

  public String extractUsername(String token) {
    return extractClaims(token).getSubject();
  }

  public boolean isValid(String token, ClientPrincipal principal) {
    Claims claims = extractClaims(token);
    return principal.getUsername().equals(claims.getSubject()) && claims.getExpiration().after(new Date());
  }

  private Claims extractClaims(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
  }
}
