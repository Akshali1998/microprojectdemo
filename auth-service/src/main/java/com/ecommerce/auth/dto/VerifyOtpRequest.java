package com.ecommerce.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to verify OTP and login")
public class VerifyOtpRequest {

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Schema(description = "User email address", example = "user@example.com")
    private String email;

    @NotBlank(message = "OTP is required")
    @Size(min = 6, max = 6, message = "OTP must be 6 digits")
    @Schema(description = "6-digit OTP received in email", example = "123456")
    private String otp;
}

