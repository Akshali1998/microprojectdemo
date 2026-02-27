package com.ecommerce.payment.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments", schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID paymentId;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(length = 50)
    private String paymentMethod; // CREDIT_CARD, DEBIT_CARD, UPI, NET_BANKING

    @Column(length = 50)
    private String status; // PENDING, SUCCESS, FAILED, CANCELLED

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime processedAt;

    @Column
    private String transactionId;

    @Column
    private String remarks;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = "PENDING";
        }
    }
}

