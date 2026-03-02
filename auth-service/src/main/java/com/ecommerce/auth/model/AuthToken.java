package com.ecommerce.auth.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "auth_tokens")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID tokenId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = true)
    private LocalDateTime revokedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public boolean isValid() {
        return revokedAt == null && LocalDateTime.now().isBefore(expiresAt);
    }
}

