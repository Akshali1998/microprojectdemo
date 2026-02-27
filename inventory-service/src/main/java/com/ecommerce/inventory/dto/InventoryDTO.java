package com.ecommerce.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDTO {
    private UUID inventoryId;
    private String productId;
    private String productName;
    private int availableQuantity;
    private int reservedQuantity;
    private int reorderLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String warehouse;
}

