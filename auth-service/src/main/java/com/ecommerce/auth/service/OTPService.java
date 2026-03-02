package com.ecommerce.auth.service;

import com.ecommerce.auth.model.OTP;
import com.ecommerce.auth.repository.OTPRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class OTPService {

    private final OTPRepository otpRepository;

    @Value("${otp.expiration}")
    private long otpExpiration;

    private static final Random RANDOM = new Random();
    private static final int OTP_LENGTH = 6;
    private static final int MAX_ATTEMPTS_PER_HOUR = 5;

    /**
     * Generate and send OTP to email
     */
    @Transactional
    public String generateOTP(String email) {
        log.info("Generating OTP for email: {}", email);

        // Check rate limiting (max 5 attempts per hour)
        long recentOtpCount = otpRepository.findAll().stream()
                .filter(o -> o.getEmail().equals(email) && !o.getIsUsed() &&
                        o.getCreatedAt().isAfter(LocalDateTime.now().minusHours(1)))
                .count();

        if (recentOtpCount >= MAX_ATTEMPTS_PER_HOUR) {
            log.warn("OTP generation rate limit exceeded for email: {}", email);
            throw new RuntimeException("Too many OTP requests. Please try again after 1 hour.");
        }

        // Generate 6-digit OTP
        String otpCode = String.format("%06d", RANDOM.nextInt(1000000));

        OTP otp = OTP.builder()
                .email(email)
                .code(otpCode)
                .expiresAt(LocalDateTime.now().plusSeconds(otpExpiration / 1000))
                .isUsed(false)
                .build();

        otpRepository.save(otp);
        log.info("OTP generated and saved for email: {}", email);

        return otpCode;
    }

    /**
     * Verify OTP
     */
    @Transactional
    public boolean verifyOTP(String email, String code) {
        log.info("Verifying OTP for email: {} with code: {}", email, code);

        Optional<OTP> otpOptional = otpRepository.findByEmailAndCodeAndIsUsedFalse(email, code);

        if (otpOptional.isEmpty()) {
            log.warn("OTP not found or already used for email: {}", email);
            return false;
        }

        OTP otp = otpOptional.get();

        // Check if OTP is expired
        if (LocalDateTime.now().isAfter(otp.getExpiresAt())) {
            log.warn("OTP has expired for email: {}", email);
            otpRepository.delete(otp);
            return false;
        }

        // Mark OTP as used
        otp.setIsUsed(true);
        otpRepository.save(otp);

        log.info("OTP verified successfully for email: {}", email);
        return true;
    }

    /**
     * Get valid OTP for email
     */
    public Optional<OTP> getValidOTP(String email) {
        return otpRepository.findLatestValidOtpByEmail(email)
                .filter(otp -> LocalDateTime.now().isBefore(otp.getExpiresAt()) && !otp.getIsUsed());
    }
}

