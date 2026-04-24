package com.challenge.swapi.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final int MIN_SECRET_LENGTH_BYTES = 32;

    @Value("${app.security.jwt.secret}")
    private String jwtSecret;

    @Value("${app.security.jwt.expiration-minutes:60}")
    private long jwtExpirationMinutes;

    public String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(jwtExpirationMinutes * 60);
        List<String> roles = authorities == null
            ? Collections.emptyList()
            : authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(this::normalizeRole)
                .distinct()
                .toList();

        return Jwts.builder()
            .subject(username)
            .claim("roles", roles)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiration))
            .signWith(signingKey())
            .compact();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        Claims claims = extractClaims(token);
        String username = claims.getSubject();
        Date expiration = claims.getExpiration();

        return username != null
            && username.equals(userDetails.getUsername())
            && userDetails.isEnabled()
            && expiration != null
            && expiration.after(new Date());
    }

    public List<String> extractRoles(String token) {
        Object roles = extractClaims(token).get("roles");
        if (roles instanceof List<?> roleList) {
            return roleList.stream()
                .map(String::valueOf)
                .map(this::normalizeRole)
                .distinct()
                .toList();
        }
        return Collections.emptyList();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
            .verifyWith(signingKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private String normalizeRole(String authority) {
        if (authority == null || authority.isBlank()) {
            return authority;
        }
        return authority.startsWith("ROLE_") ? authority.substring("ROLE_".length()) : authority;
    }

    private SecretKey signingKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < MIN_SECRET_LENGTH_BYTES) {
            throw new IllegalStateException("JWT secret must be at least 32 bytes long");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
