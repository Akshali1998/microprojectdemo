package com.ecommerce.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private UUID paymentId;
    private UUID orderId;
    private UUID customerId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String transactionId;
    private String remarks;
}

