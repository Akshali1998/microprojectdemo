package com.ecommerce.inventory.service;

import com.ecommerce.inventory.dto.InventoryDTO;
import com.ecommerce.inventory.model.Inventory;
import com.ecommerce.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public InventoryDTO createInventory(InventoryDTO inventoryDTO) {
        log.info("Creating inventory for productId: {}", inventoryDTO.getProductId());

        Inventory inventory = Inventory.builder()
                .productId(inventoryDTO.getProductId())
                .productName(inventoryDTO.getProductName())
                .availableQuantity(inventoryDTO.getAvailableQuantity())
                .reservedQuantity(0)
                .reorderLevel(inventoryDTO.getReorderLevel())
                .warehouse(inventoryDTO.getWarehouse())
                .build();

        Inventory savedInventory = inventoryRepository.save(inventory);
        log.info("Inventory created for productId: {}", savedInventory.getProductId());

        return mapToDTO(savedInventory);
    }

    public InventoryDTO getInventoryById(UUID inventoryId) {
        log.info("Fetching inventory with inventoryId: {}", inventoryId);
        return inventoryRepository.findById(inventoryId)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public InventoryDTO getInventoryByProductId(String productId) {
        log.info("Fetching inventory for productId: {}", productId);
        return inventoryRepository.findByProductId(productId)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public List<InventoryDTO> getAllInventory() {
        log.info("Fetching all inventory");
        return inventoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<InventoryDTO> getLowStockItems() {
        log.info("Fetching low stock items");
        return inventoryRepository.findLowStockItems().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<InventoryDTO> getInventoryByWarehouse(String warehouse) {
        log.info("Fetching inventory for warehouse: {}", warehouse);
        return inventoryRepository.findByWarehouse(warehouse).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public InventoryDTO reserveStock(String productId, int quantity) {
        log.info("Reserving {} units of productId: {}", quantity, productId);

        Optional<Inventory> inventory = inventoryRepository.findByProductId(productId);
        if (inventory.isPresent()) {
            Inventory item = inventory.get();
            if (item.getAvailableQuantity() >= quantity) {
                item.setAvailableQuantity(item.getAvailableQuantity() - quantity);
                item.setReservedQuantity(item.getReservedQuantity() + quantity);
                Inventory updatedInventory = inventoryRepository.save(item);
                log.info("Reserved {} units of productId: {}", quantity, productId);
                return mapToDTO(updatedInventory);
            } else {
                log.warn("Insufficient stock for productId: {}", productId);
            }
        }
        return null;
    }

    @Transactional
    public InventoryDTO releaseStock(String productId, int quantity) {
        log.info("Releasing {} units of productId: {}", quantity, productId);

        Optional<Inventory> inventory = inventoryRepository.findByProductId(productId);
        if (inventory.isPresent()) {
            Inventory item = inventory.get();
            if (item.getReservedQuantity() >= quantity) {
                item.setReservedQuantity(item.getReservedQuantity() - quantity);
                item.setAvailableQuantity(item.getAvailableQuantity() + quantity);
                Inventory updatedInventory = inventoryRepository.save(item);
                log.info("Released {} units of productId: {}", quantity, productId);
                return mapToDTO(updatedInventory);
            }
        }
        return null;
    }

    @Transactional
    public InventoryDTO addStock(String productId, int quantity) {
        log.info("Adding {} units to productId: {}", quantity, productId);

        Optional<Inventory> inventory = inventoryRepository.findByProductId(productId);
        if (inventory.isPresent()) {
            Inventory item = inventory.get();
            item.setAvailableQuantity(item.getAvailableQuantity() + quantity);
            Inventory updatedInventory = inventoryRepository.save(item);
            log.info("Added {} units to productId: {}", quantity, productId);
            return mapToDTO(updatedInventory);
        }
        return null;
    }

    public long countLowStockItems() {
        return inventoryRepository.countLowStockItems();
    }

    private InventoryDTO mapToDTO(Inventory inventory) {
        return new InventoryDTO(
                inventory.getInventoryId(),
                inventory.getProductId(),
                inventory.getProductName(),
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity(),
                inventory.getReorderLevel(),
                inventory.getCreatedAt(),
                inventory.getUpdatedAt(),
                inventory.getWarehouse()
        );
    }
}

