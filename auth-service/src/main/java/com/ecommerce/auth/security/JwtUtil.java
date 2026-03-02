package com.ecommerce.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Generate JWT token for user
     */
    public String generateToken(UUID userId, String email, String role) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpiration);

            return Jwts.builder()
                    .setSubject(userId.toString())
                    .claim("email", email)
                    .claim("role", role)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();
        } catch (Exception e) {
            log.error("Error generating JWT token", e);
            throw new RuntimeException("Could not generate JWT token", e);
        }
    }

    /**
     * Get user ID from token
     */
    public UUID getUserIdFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return UUID.fromString(claims.getSubject());
        } catch (Exception e) {
            log.error("Error extracting user ID from token", e);
            return null;
        }
    }

    /**
     * Get email from token
     */
    public String getEmailFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return (String) claims.get("email");
        } catch (Exception e) {
            log.error("Error extracting email from token", e);
            return null;
        }
    }

    /**
     * Get role from token
     */
    public String getRoleFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return (String) claims.get("role");
        } catch (Exception e) {
            log.error("Error extracting role from token", e);
            return null;
        }
    }

    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get all claims from token
     */
    private Claims getAllClaimsFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Error extracting claims from token", e);
            throw new RuntimeException("Could not extract claims from token", e);
        }
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Get expiry date from token
     */
    public Date getExpiryDateFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.getExpiration();
        } catch (Exception e) {
            return null;
        }
    }
}

