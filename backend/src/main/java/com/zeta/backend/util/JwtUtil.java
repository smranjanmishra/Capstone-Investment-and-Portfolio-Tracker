package com.zeta.backend.util;

import com.zeta.backend.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


// Utility class for JWT token operations.
// Handles token generation, validation, and claim extraction using HS256 algorithm.

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

//     generate signing key from secret string.
//     uses HMAC-SHA for HS256 algorithm.
//     return SecretKey for signing tokens

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

//     generate JWT token for a user.
//     token includes userId and role claims and expires after configured duration (30 minutes).
//     userId -> the user's ID
//     role -> the user's role
//     return generated JWT token string

    public String generateToken(Long userId, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role.name());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        String token = Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();

        logger.info("JWT token generated for userId: {}, role: {}", userId, role);
        return token;
    }

//     validate JWT token.
//     checks signature and expiration.
//     token -> the JWT token to validate
//     return true if token is valid, false otherwise

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            logger.warn("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }

//     extract all claims from JWT token.
//     token -> the JWT token
//     return claims object containing all token claims

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

//     extract userId from JWT token.
//     token -> the JWT token
//     return user ID from token claims

    public Long extractUserId(String token) {
        Claims claims = extractClaims(token);
        return claims.get("userId", Long.class);
    }

//     extract role from JWT token.
//     token -> the JWT token
//     return Role enum from token claims

    public Role extractRole(String token) {
        Claims claims = extractClaims(token);
        String roleString = claims.get("role", String.class);
        return Role.valueOf(roleString);
    }

//     check if token is expired.
//     token the JWT token
//     return true if expired, false otherwise

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
