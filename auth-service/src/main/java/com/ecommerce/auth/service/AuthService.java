package com.ecommerce.auth.service;

import com.ecommerce.auth.dto.AuthResponse;
import com.ecommerce.auth.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final OTPService otpService;
    private final TokenService tokenService;

    /**
     * Send OTP to email
     */
    @Transactional
    public String sendOtp(String email) {
        log.info("Sending OTP for email: {}", email);

        // Create user if not exists
        User user = userService.createOrGetUserByEmail(email);

        // Generate OTP
        String otp = otpService.generateOTP(email);

        // TODO: Integrate with Notification Service to send email
        log.info("OTP generated: {} (In production, this should be sent via email)", otp);

        return otp; // In production, don't return OTP to client. This is for testing only.
    }

    /**
     * Verify OTP and login user
     */
    @Transactional
    public AuthResponse verifyOtpAndLogin(String email, String otp) {
        log.info("Verifying OTP and logging in for email: {}", email);

        // Verify OTP
        if (!otpService.verifyOTP(email, otp)) {
            log.warn("OTP verification failed for email: {}", email);
            throw new RuntimeException("Invalid or expired OTP");
        }

        // Get or create user
        User user = userService.createOrGetUserByEmail(email);

        if (!user.getIsActive()) {
            log.warn("User account is inactive: {}", email);
            throw new RuntimeException("User account is inactive");
        }

        // Create JWT token
        com.ecommerce.auth.model.AuthToken authToken = tokenService.createToken(
                user.getUserId(),
                user.getEmail(),
                user.getRole()
        );

        log.info("User logged in successfully: {}", email);

        return AuthResponse.builder()
                .token(authToken.getToken())
                .userId(user.getUserId())
                .email(user.getEmail())
                .role(user.getRole())
                .expiresAt(authToken.getExpiresAt())
                .message("Login successful")
                .success(true)
                .build();
    }

    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        log.info("Validating token");
        return tokenService.validateToken(token);
    }

    /**
     * Get user from token
     */
    public User getUserFromToken(String token) {
        UUID userId = tokenService.getUserIdFromToken(token);
        return userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Logout user (revoke token)
     */
    @Transactional
    public void logout(String token) {
        log.info("Logging out user");
        tokenService.revokeToken(token);
    }

    /**
     * Logout user from all devices
     */
    @Transactional
    public void logoutAll(UUID userId) {
        log.info("Logging out user from all devices: {}", userId);
        tokenService.revokeAllUserTokens(userId);
    }
}

