package com.ecommerce.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User information")
public class UserDTO {

    @Schema(description = "User ID")
    private UUID userId;

    @Schema(description = "User email")
    private String email;

    @Schema(description = "User role")
    private String role;

    @Schema(description = "User active status")
    private Boolean isActive;
}

