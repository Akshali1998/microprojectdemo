package com.ecommerce.payment.repository;

import com.ecommerce.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByOrderId(UUID orderId);

    List<Payment> findByCustomerId(UUID customerId);

    @Query("SELECT p FROM Payment p WHERE p.status = :status")
    List<Payment> findByStatus(@Param("status") String status);

    Optional<Payment> findByTransactionId(String transactionId);

    long countByStatus(String status);
}

