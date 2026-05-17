package com.ogc_prototype.ogc.config;

import com.ogc_prototype.ogc.model.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24h por defecto
    private long expiration;

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    // Solo incluimos lo mínimo: id, username y role
    // El JWT es base64
    public String generateToken(Integer userId, String username, Role role) {
        return Jwts.builder().subject(String.valueOf(userId)).claim("username", username)
                .claim("role", role.name()).issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(signingKey()).compact();
    }

    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Integer extractUserId(String token) {
        return Integer.valueOf(extractClaims(token).getSubject());
    }

    public String extractUsername(String token) {
        return extractClaims(token).get("username", String.class);
    }

    public Role extractRole(String token) {
        return Role.valueOf(extractClaims(token).get("role", String.class));
    }

    private Claims extractClaims(String token) {
        return Jwts.parser().verifyWith(signingKey()).build().parseSignedClaims(token).getPayload();
    }
}
