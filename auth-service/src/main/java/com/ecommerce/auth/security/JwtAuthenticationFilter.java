package com.ecommerce.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String bearerToken = getJwtFromRequest(request);

            if (bearerToken != null && jwtUtil.validateToken(bearerToken)) {
                String userId = jwtUtil.getUserIdFromToken(bearerToken).toString();
                String email = jwtUtil.getEmailFromToken(bearerToken);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId, null, new ArrayList<>()
                );
                authentication.setDetails(email);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Forward user info to downstream services
                request.setAttribute("X-User-Id", userId);
                request.setAttribute("X-User-Email", email);
            }
        } catch (Exception e) {
            log.error("JWT authentication failed", e);
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
}

