package com.ecommerce.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Authentication response with JWT token")
public class AuthResponse {

    @Schema(description = "JWT authentication token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "User ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID userId;

    @Schema(description = "User email", example = "user@example.com")
    private String email;

    @Schema(description = "User role", example = "USER")
    private String role;

    @Schema(description = "Token expiry time")
    private LocalDateTime expiresAt;

    @Schema(description = "Response message", example = "Login successful")
    private String message;

    @Schema(description = "Success status")
    private boolean success;
}

