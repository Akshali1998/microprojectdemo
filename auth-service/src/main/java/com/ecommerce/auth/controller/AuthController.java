package com.ecommerce.auth.controller;

import com.ecommerce.auth.dto.ApiResponse;
import com.ecommerce.auth.dto.AuthResponse;
import com.ecommerce.auth.dto.SendOtpRequest;
import com.ecommerce.auth.dto.VerifyOtpRequest;
import com.ecommerce.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and OTP verification endpoints")
public class AuthController {

    private final AuthService authService;

    /**
     * Send OTP to email
     */
    @PostMapping("/send-otp")
    @Operation(
            summary = "Send OTP to email",
            description = "Generates and sends a 6-digit OTP to the provided email address"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "OTP sent successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid email or too many requests",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        log.info("Sending OTP to email: {}", request.getEmail());

        try {
            String otp = authService.sendOtp(request.getEmail());

            ApiResponse response = ApiResponse.builder()
                    .message("OTP sent successfully to " + request.getEmail())
                    .success(true)
                    .data(otp) // In production, don't return OTP
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error sending OTP", e);

            ApiResponse response = ApiResponse.builder()
                    .message("Failed to send OTP")
                    .success(false)
                    .error(e.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Verify OTP and login
     */
    @PostMapping("/verify-otp")
    @Operation(
            summary = "Verify OTP and login",
            description = "Verifies the OTP and returns JWT token if successful"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Login successful, JWT token returned",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid or expired OTP",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        log.info("Verifying OTP for email: {}", request.getEmail());

        try {
            AuthResponse authResponse = authService.verifyOtpAndLogin(request.getEmail(), request.getOtp());

            ApiResponse response = ApiResponse.builder()
                    .message("Login successful")
                    .success(true)
                    .data(authResponse)
                    .build();

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("OTP verification failed", e);

            ApiResponse response = ApiResponse.builder()
                    .message("Login failed")
                    .success(false)
                    .error(e.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            log.error("Error verifying OTP", e);

            ApiResponse response = ApiResponse.builder()
                    .message("An error occurred")
                    .success(false)
                    .error(e.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Validate token
     */
    @PostMapping("/validate-token")
    @Operation(
            summary = "Validate JWT token",
            description = "Validates the provided JWT token"
    )
    public ResponseEntity<ApiResponse> validateToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        log.info("Validating token");

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                ApiResponse response = ApiResponse.builder()
                        .message("Invalid token format")
                        .success(false)
                        .error("Authorization header must be in format: Bearer <token>")
                        .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            String token = authHeader.substring(7);
            boolean isValid = authService.validateToken(token);

            ApiResponse response = ApiResponse.builder()
                    .message(isValid ? "Token is valid" : "Token is invalid")
                    .success(isValid)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error validating token", e);

            ApiResponse response = ApiResponse.builder()
                    .message("Token validation failed")
                    .success(false)
                    .error(e.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get current user info
     */
    @GetMapping("/me")
    @Operation(
            summary = "Get current user information",
            description = "Returns information about the authenticated user"
    )
    public ResponseEntity<ApiResponse> getCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        log.info("Getting current user info");

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                ApiResponse response = ApiResponse.builder()
                        .message("Invalid token format")
                        .success(false)
                        .error("Authorization header must be in format: Bearer <token>")
                        .build();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = authHeader.substring(7);
            var user = authService.getUserFromToken(token);

            ApiResponse response = ApiResponse.builder()
                    .message("User information retrieved successfully")
                    .success(true)
                    .data(user)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting current user", e);

            ApiResponse response = ApiResponse.builder()
                    .message("Failed to retrieve user information")
                    .success(false)
                    .error(e.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * Logout user
     */
    @PostMapping("/logout")
    @Operation(
            summary = "Logout user",
            description = "Revokes the current JWT token"
    )
    public ResponseEntity<ApiResponse> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        log.info("User logging out");

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                ApiResponse response = ApiResponse.builder()
                        .message("Invalid token format")
                        .success(false)
                        .error("Authorization header must be in format: Bearer <token>")
                        .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            String token = authHeader.substring(7);
            authService.logout(token);

            ApiResponse response = ApiResponse.builder()
                    .message("Logout successful")
                    .success(true)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error during logout", e);

            ApiResponse response = ApiResponse.builder()
                    .message("Logout failed")
                    .success(false)
                    .error(e.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

