package com.ecommerce.auth.service;

import com.ecommerce.auth.model.AuthToken;
import com.ecommerce.auth.repository.AuthTokenRepository;
import com.ecommerce.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final AuthTokenRepository authTokenRepository;
    private final JwtUtil jwtUtil;

    /**
     * Create and save JWT token
     */
    @Transactional
    public AuthToken createToken(UUID userId, String email, String role) {
        log.info("Creating token for user: {}", userId);

        String jwtToken = jwtUtil.generateToken(userId, email, role);
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(900); // 15 minutes

        AuthToken authToken = AuthToken.builder()
                .userId(userId)
                .token(jwtToken)
                .expiresAt(expiresAt)
                .build();

        AuthToken savedToken = authTokenRepository.save(authToken);
        log.info("Token created for user: {}", userId);
        return savedToken;
    }

    /**
     * Validate token
     */
    public boolean validateToken(String token) {
        log.info("Validating token");

        if (!jwtUtil.validateToken(token)) {
            log.warn("JWT validation failed");
            return false;
        }

        Optional<AuthToken> authToken = authTokenRepository.findByToken(token);
        if (authToken.isEmpty()) {
            log.warn("Token not found in database");
            return false;
        }

        AuthToken token_entity = authToken.get();
        if (!token_entity.isValid()) {
            log.warn("Token is revoked or expired");
            return false;
        }

        return true;
    }

    /**
     * Get token by JWT string
     */
    public Optional<AuthToken> getTokenByJwt(String jwtToken) {
        return authTokenRepository.findByToken(jwtToken);
    }

    /**
     * Revoke token
     */
    @Transactional
    public void revokeToken(String jwtToken) {
        log.info("Revoking token");
        authTokenRepository.findByToken(jwtToken).ifPresent(token -> {
            token.setRevokedAt(LocalDateTime.now());
            authTokenRepository.save(token);
            log.info("Token revoked for user: {}", token.getUserId());
        });
    }

    /**
     * Revoke all tokens for user
     */
    @Transactional
    public void revokeAllUserTokens(UUID userId) {
        log.info("Revoking all tokens for user: {}", userId);
        authTokenRepository.findAll().stream()
                .filter(token -> token.getUserId().equals(userId) && token.getRevokedAt() == null)
                .forEach(token -> {
                    token.setRevokedAt(LocalDateTime.now());
                    authTokenRepository.save(token);
                });
        log.info("All tokens revoked for user: {}", userId);
    }

    /**
     * Get JWT claims
     */
    public UUID getUserIdFromToken(String token) {
        return jwtUtil.getUserIdFromToken(token);
    }

    public String getEmailFromToken(String token) {
        return jwtUtil.getEmailFromToken(token);
    }

    public String getRoleFromToken(String token) {
        return jwtUtil.getRoleFromToken(token);
    }
}

