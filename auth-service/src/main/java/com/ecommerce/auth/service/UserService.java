package com.ecommerce.auth.service;

import com.ecommerce.auth.model.User;
import com.ecommerce.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        log.info("Finding user with email: {}", email);
        return userRepository.findByEmail(email);
    }

    /**
     * Find user by ID
     */
    public Optional<User> findById(UUID userId) {
        log.info("Finding user with ID: {}", userId);
        return userRepository.findById(userId);
    }

    /**
     * Check if user exists by email
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Create or get user by email
     */
    @Transactional
    public User createOrGetUserByEmail(String email) {
        log.info("Creating or getting user for email: {}", email);
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            log.info("User already exists with email: {}", email);
            return existingUser.get();
        }

        User newUser = User.builder()
                .email(email)
                .passwordHash("") // Empty password for OTP-only login
                .role("USER") // Default role
                .isActive(true)
                .build();

        User savedUser = userRepository.save(newUser);
        log.info("New user created with ID: {}", savedUser.getUserId());
        return savedUser;
    }

    /**
     * Activate user
     */
    @Transactional
    public void activateUser(UUID userId) {
        log.info("Activating user with ID: {}", userId);
        userRepository.findById(userId).ifPresent(user -> {
            user.setIsActive(true);
            userRepository.save(user);
            log.info("User activated: {}", userId);
        });
    }

    /**
     * Deactivate user
     */
    @Transactional
    public void deactivateUser(UUID userId) {
        log.info("Deactivating user with ID: {}", userId);
        userRepository.findById(userId).ifPresent(user -> {
            user.setIsActive(false);
            userRepository.save(user);
            log.info("User deactivated: {}", userId);
        });
    }
}

