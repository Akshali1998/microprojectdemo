package com.ecommerce.payment.service;

import com.ecommerce.payment.dto.PaymentDTO;
import com.ecommerce.payment.model.Payment;
import com.ecommerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        log.info("Creating payment for orderId: {}", paymentDTO.getOrderId());

        Payment payment = Payment.builder()
                .orderId(paymentDTO.getOrderId())
                .customerId(paymentDTO.getCustomerId())
                .amount(paymentDTO.getAmount())
                .paymentMethod(paymentDTO.getPaymentMethod())
                .status("PENDING")
                .remarks(paymentDTO.getRemarks())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created with paymentId: {}", savedPayment.getPaymentId());

        return mapToDTO(savedPayment);
    }

    public PaymentDTO getPaymentById(UUID paymentId) {
        log.info("Fetching payment with paymentId: {}", paymentId);
        return paymentRepository.findById(paymentId)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public PaymentDTO getPaymentByOrderId(UUID orderId) {
        log.info("Fetching payment for orderId: {}", orderId);
        return paymentRepository.findByOrderId(orderId)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public List<PaymentDTO> getAllPayments() {
        log.info("Fetching all payments");
        return paymentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> getPaymentsByCustomerId(UUID customerId) {
        log.info("Fetching payments for customerId: {}", customerId);
        return paymentRepository.findByCustomerId(customerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> getPaymentsByStatus(String status) {
        log.info("Fetching payments with status: {}", status);
        return paymentRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentDTO processPayment(UUID paymentId, String paymentMethod) {
        log.info("Processing payment with paymentId: {}", paymentId);

        Optional<Payment> payment = paymentRepository.findById(paymentId);
        if (payment.isPresent()) {
            Payment existingPayment = payment.get();
            existingPayment.setStatus("SUCCESS");
            existingPayment.setProcessedAt(LocalDateTime.now());
            existingPayment.setTransactionId("TXN-" + UUID.randomUUID());
            Payment updatedPayment = paymentRepository.save(existingPayment);
            log.info("Payment {} processed successfully", paymentId);
            return mapToDTO(updatedPayment);
        }
        return null;
    }

    @Transactional
    public PaymentDTO updatePaymentStatus(UUID paymentId, String status) {
        log.info("Updating payment {} status to {}", paymentId, status);

        Optional<Payment> payment = paymentRepository.findById(paymentId);
        if (payment.isPresent()) {
            Payment existingPayment = payment.get();
            existingPayment.setStatus(status);
            if ("SUCCESS".equals(status)) {
                existingPayment.setProcessedAt(LocalDateTime.now());
                existingPayment.setTransactionId("TXN-" + UUID.randomUUID());
            }
            Payment updatedPayment = paymentRepository.save(existingPayment);
            log.info("Payment {} status updated to {}", paymentId, status);
            return mapToDTO(updatedPayment);
        }
        return null;
    }

    @Transactional
    public void deletePayment(UUID paymentId) {
        log.info("Deleting payment: {}", paymentId);
        paymentRepository.deleteById(paymentId);
    }

    public long getTotalPaymentsByStatus(String status) {
        return paymentRepository.countByStatus(status);
    }

    private PaymentDTO mapToDTO(Payment payment) {
        return new PaymentDTO(
                payment.getPaymentId(),
                payment.getOrderId(),
                payment.getCustomerId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getCreatedAt(),
                payment.getProcessedAt(),
                payment.getTransactionId(),
                payment.getRemarks()
        );
    }
}

