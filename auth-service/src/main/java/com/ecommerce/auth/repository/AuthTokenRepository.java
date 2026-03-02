package com.ecommerce.auth.repository;

import com.ecommerce.auth.model.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, UUID> {

    Optional<AuthToken> findByToken(String token);

    Optional<AuthToken> findByUserIdAndRevokedAtIsNull(UUID userId);
}

