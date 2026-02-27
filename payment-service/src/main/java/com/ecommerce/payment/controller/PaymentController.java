package com.ecommerce.payment.controller;

import com.ecommerce.payment.dto.PaymentDTO;
import com.ecommerce.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(@RequestBody PaymentDTO paymentDTO) {
        log.info("POST request to create payment for orderId: {}", paymentDTO.getOrderId());
        PaymentDTO createdPayment = paymentService.createPayment(paymentDTO);
        return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable UUID paymentId) {
        log.info("GET request for paymentId: {}", paymentId);
        PaymentDTO payment = paymentService.getPaymentById(paymentId);
        if (payment != null) {
            return ResponseEntity.ok(payment);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentDTO> getPaymentByOrderId(@PathVariable UUID orderId) {
        log.info("GET request for orderId: {}", orderId);
        PaymentDTO payment = paymentService.getPaymentByOrderId(orderId);
        if (payment != null) {
            return ResponseEntity.ok(payment);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        log.info("GET request to fetch all payments");
        List<PaymentDTO> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByCustomerId(@PathVariable UUID customerId) {
        log.info("GET request for customerId: {}", customerId);
        List<PaymentDTO> payments = paymentService.getPaymentsByCustomerId(customerId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByStatus(@PathVariable String status) {
        log.info("GET request for status: {}", status);
        List<PaymentDTO> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/{paymentId}/process")
    public ResponseEntity<PaymentDTO> processPayment(
            @PathVariable UUID paymentId,
            @RequestParam String paymentMethod) {
        log.info("POST request to process payment: {}", paymentId);
        PaymentDTO processedPayment = paymentService.processPayment(paymentId, paymentMethod);
        if (processedPayment != null) {
            return ResponseEntity.ok(processedPayment);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{paymentId}/status")
    public ResponseEntity<PaymentDTO> updatePaymentStatus(
            @PathVariable UUID paymentId,
            @RequestParam String status) {
        log.info("PATCH request to update payment {} status to {}", paymentId, status);
        PaymentDTO updatedPayment = paymentService.updatePaymentStatus(paymentId, status);
        if (updatedPayment != null) {
            return ResponseEntity.ok(updatedPayment);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Void> deletePayment(@PathVariable UUID paymentId) {
        log.info("DELETE request for paymentId: {}", paymentId);
        paymentService.deletePayment(paymentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/status/{status}")
    public ResponseEntity<Long> getTotalPaymentsByStatus(@PathVariable String status) {
        log.info("GET request for total payments with status: {}", status);
        long count = paymentService.getTotalPaymentsByStatus(status);
        return ResponseEntity.ok(count);
    }
}

