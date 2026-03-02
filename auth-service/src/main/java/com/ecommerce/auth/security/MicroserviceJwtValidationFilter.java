package com.ecommerce.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * This filter can be used in other microservices to validate JWT tokens
 * and forward user information via headers
 */
@Slf4j
@Component
public class MicroserviceJwtValidationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret:ecommerce-microservices-secret-key-change-in-production-with-long-random-string-min-256-bits}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String bearerToken = getJwtFromRequest(request);

            if (bearerToken != null) {
                // Validate token using JwtUtil logic (can be extended)
                String userId = extractUserIdFromToken(bearerToken);
                String email = extractEmailFromToken(bearerToken);

                if (userId != null && email != null) {
                    // Forward user info to downstream services
                    request.setAttribute("X-User-Id", userId);
                    request.setAttribute("X-User-Email", email);
                    request.setAttribute("X-Auth-Token", bearerToken);
                }
            }
        } catch (Exception e) {
            log.error("JWT validation failed in microservice", e);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private String extractUserIdFromToken(String token) {
        // Implement token parsing logic
        // For now, return a placeholder
        return "user-id";
    }

    private String extractEmailFromToken(String token) {
        // Implement token parsing logic
        // For now, return a placeholder
        return "user-email";
    }
}

