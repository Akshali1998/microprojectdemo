package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderDTO;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.repository.OrderRepository;
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
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        log.info("Creating order for customerId: {}", orderDTO.getCustomerId());

        Order order = Order.builder()
                .customerId(orderDTO.getCustomerId())
                .totalAmount(orderDTO.getTotalAmount())
                .status("PENDING")
                .shippingAddress(orderDTO.getShippingAddress())
                .billingAddress(orderDTO.getBillingAddress())
                .notes(orderDTO.getNotes())
                .build();

        Order savedOrder = orderRepository.save(order);
        log.info("Order created with orderId: {}", savedOrder.getOrderId());

        return mapToDTO(savedOrder);
    }

    public OrderDTO getOrderById(UUID orderId) {
        log.info("Fetching order with orderId: {}", orderId);
        return orderRepository.findById(orderId)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public List<OrderDTO> getAllOrders() {
        log.info("Fetching all orders");
        return orderRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersByCustomerId(UUID customerId) {
        log.info("Fetching orders for customerId: {}", customerId);
        return orderRepository.findByCustomerId(customerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersByStatus(String status) {
        log.info("Fetching orders with status: {}", status);
        return orderRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO updateOrderStatus(UUID orderId, String newStatus) {
        log.info("Updating order {} status to {}", orderId, newStatus);

        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            Order existingOrder = order.get();
            existingOrder.setStatus(newStatus);
            Order updatedOrder = orderRepository.save(existingOrder);
            log.info("Order {} status updated to {}", orderId, newStatus);
            return mapToDTO(updatedOrder);
        }
        return null;
    }

    @Transactional
    public OrderDTO updateOrder(UUID orderId, OrderDTO orderDTO) {
        log.info("Updating order: {}", orderId);

        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            Order existingOrder = order.get();
            existingOrder.setShippingAddress(orderDTO.getShippingAddress());
            existingOrder.setBillingAddress(orderDTO.getBillingAddress());
            existingOrder.setNotes(orderDTO.getNotes());
            existingOrder.setTotalAmount(orderDTO.getTotalAmount());
            Order updatedOrder = orderRepository.save(existingOrder);
            log.info("Order {} updated", orderId);
            return mapToDTO(updatedOrder);
        }
        return null;
    }

    @Transactional
    public void deleteOrder(UUID orderId) {
        log.info("Deleting order: {}", orderId);
        orderRepository.deleteById(orderId);
    }

    public long getTotalOrdersByStatus(String status) {
        return orderRepository.countByStatus(status);
    }

    private OrderDTO mapToDTO(Order order) {
        return new OrderDTO(
                order.getOrderId(),
                order.getCustomerId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getShippingAddress(),
                order.getBillingAddress(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getNotes()
        );
    }
}

