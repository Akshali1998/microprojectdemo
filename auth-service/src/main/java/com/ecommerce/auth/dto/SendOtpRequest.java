package com.ecommerce.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to send OTP to email")
public class SendOtpRequest {

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Schema(description = "User email address", example = "user@example.com")
    private String email;
}

