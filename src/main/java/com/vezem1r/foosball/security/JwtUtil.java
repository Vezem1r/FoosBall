package com.vezem1r.foosball.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private final SecretKey secretKey;
  private final long jwtExpiration;

  public JwtUtil(
      @Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long jwtExpiration) {
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    this.jwtExpiration = jwtExpiration;
  }

  public String generateToken(Long userId, String username, String role) {
    Instant now = Instant.now();
    return Jwts.builder()
        .subject(userId.toString())
        .claim("username", username)
        .claim("role", role)
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plus(jwtExpiration, ChronoUnit.SECONDS)))
        .signWith(secretKey)
        .compact();
  }

  public Claims extractClaims(String token) {
    try {
      return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    } catch (JwtException e) {
      throw new RuntimeException("Invalid JWT token", e);
    }
  }

  public Long extractUserId(String token) {
    return Long.valueOf(extractClaims(token).getSubject());
  }

  public String extractUsername(String token) {
    return extractClaims(token).get("username", String.class);
  }

  public String extractRole(String token) {
    return extractClaims(token).get("role", String.class);
  }

  public boolean isTokenExpired(String token) {
    try {
      return extractClaims(token).getExpiration().before(new Date());
    } catch (Exception e) {
      return true;
    }
  }

  public boolean validateToken(String token) {
    try {
      extractClaims(token);
      return !isTokenExpired(token);
    } catch (Exception e) {
      return false;
    }
  }
}
