package com.ecommerce.auth.repository;

import com.ecommerce.auth.model.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OTPRepository extends JpaRepository<OTP, UUID> {

    @Query("SELECT o FROM OTP o WHERE o.email = :email AND o.isUsed = false ORDER BY o.createdAt DESC LIMIT 1")
    Optional<OTP> findLatestValidOtpByEmail(@Param("email") String email);

    Optional<OTP> findByEmailAndCodeAndIsUsedFalse(String email, String code);

    void deleteByEmailAndCode(String email, String code);
}

