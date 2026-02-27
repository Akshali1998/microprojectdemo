package com.ecommerce.inventory.controller;

import com.ecommerce.inventory.dto.ApiResponse;
import com.ecommerce.inventory.dto.InventoryDTO;
import com.ecommerce.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<InventoryDTO>> createInventory(@RequestBody InventoryDTO inventoryDTO) {
        log.info("POST request to create inventory for productId: {}", inventoryDTO.getProductId());
        try {
            InventoryDTO createdInventory = inventoryService.createInventory(inventoryDTO);
            if (createdInventory != null) {
                ApiResponse<InventoryDTO> resp = ApiResponse.success("Inventory created", HttpStatus.CREATED.value(), createdInventory);
                return new ResponseEntity<>(resp, HttpStatus.CREATED);
            }
            ApiResponse<InventoryDTO> error = ApiResponse.failure("Failed to create inventory", HttpStatus.INTERNAL_SERVER_ERROR.value(), "INVENTORY_CREATE_ERROR", "NA");
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            log.error("Exception creating inventory", ex);
            ApiResponse<InventoryDTO> error = ApiResponse.failure("Failed to create inventory", HttpStatus.INTERNAL_SERVER_ERROR.value(), "INVENTORY_CREATE_EXCEPTION", ex.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{inventoryId}")
    public ResponseEntity<ApiResponse<InventoryDTO>> getInventoryById(@PathVariable UUID inventoryId) {
        log.info("GET request for inventoryId: {}", inventoryId);
        try {
            InventoryDTO inventory = inventoryService.getInventoryById(inventoryId);
            if (inventory != null) {
                return ResponseEntity.ok(ApiResponse.success("Inventory fetched", HttpStatus.OK.value(), inventory));
            }
            return new ResponseEntity<>(ApiResponse.failure("Inventory not found", HttpStatus.NOT_FOUND.value(), "INVENTORY_NOT_FOUND", "NA"), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            log.error("Exception fetching inventory by id", ex);
            return new ResponseEntity<>(ApiResponse.failure("Failed to fetch inventory", HttpStatus.INTERNAL_SERVER_ERROR.value(), "INVENTORY_FETCH_EXCEPTION", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<InventoryDTO>> getInventoryByProductId(@PathVariable String productId) {
        log.info("GET request for productId: {}", productId);
        try {
            InventoryDTO inventory = inventoryService.getInventoryByProductId(productId);
            if (inventory != null) {
                return ResponseEntity.ok(ApiResponse.success("Inventory fetched", HttpStatus.OK.value(), inventory));
            }
            return new ResponseEntity<>(ApiResponse.failure("Inventory not found for product", HttpStatus.NOT_FOUND.value(), "INVENTORY_NOT_FOUND", "NA"), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            log.error("Exception fetching inventory by productId", ex);
            return new ResponseEntity<>(ApiResponse.failure("Failed to fetch inventory", HttpStatus.INTERNAL_SERVER_ERROR.value(), "INVENTORY_FETCH_EXCEPTION", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventoryDTO>>> getAllInventory() {
        log.info("GET request to fetch all inventory");
        try {
            List<InventoryDTO> inventory = inventoryService.getAllInventory();
            return ResponseEntity.ok(ApiResponse.success("Inventory list fetched", HttpStatus.OK.value(), inventory));
        } catch (Exception ex) {
            log.error("Exception fetching all inventory", ex);
            return new ResponseEntity<>(ApiResponse.failure("Failed to fetch inventory list", HttpStatus.INTERNAL_SERVER_ERROR.value(), "INVENTORY_LIST_EXCEPTION", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<InventoryDTO>>> getLowStockItems() {
        log.info("GET request to fetch low stock items");
        try {
            List<InventoryDTO> inventory = inventoryService.getLowStockItems();
            return ResponseEntity.ok(ApiResponse.success("Low stock items fetched", HttpStatus.OK.value(), inventory));
        } catch (Exception ex) {
            log.error("Exception fetching low stock items", ex);
            return new ResponseEntity<>(ApiResponse.failure("Failed to fetch low stock items", HttpStatus.INTERNAL_SERVER_ERROR.value(), "INVENTORY_LOW_STOCK_EXCEPTION", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/warehouse/{warehouse}")
    public ResponseEntity<ApiResponse<List<InventoryDTO>>> getInventoryByWarehouse(@PathVariable String warehouse) {
        log.info("GET request for warehouse: {}", warehouse);
        try {
            List<InventoryDTO> inventory = inventoryService.getInventoryByWarehouse(warehouse);
            return ResponseEntity.ok(ApiResponse.success("Inventory by warehouse fetched", HttpStatus.OK.value(), inventory));
        } catch (Exception ex) {
            log.error("Exception fetching inventory by warehouse", ex);
            return new ResponseEntity<>(ApiResponse.failure("Failed to fetch inventory by warehouse", HttpStatus.INTERNAL_SERVER_ERROR.value(), "INVENTORY_WAREHOUSE_EXCEPTION", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/reserve/{productId}")
    public ResponseEntity<ApiResponse<InventoryDTO>> reserveStock(
            @PathVariable String productId,
            @RequestParam int quantity) {
        log.info("POST request to reserve {} units of productId: {}", quantity, productId);
        try {
            InventoryDTO updatedInventory = inventoryService.reserveStock(productId, quantity);
            if (updatedInventory != null) {
                return ResponseEntity.ok(ApiResponse.success("Stock reserved", HttpStatus.OK.value(), updatedInventory));
            }
            return new ResponseEntity<>(ApiResponse.failure("Insufficient stock to reserve", HttpStatus.BAD_REQUEST.value(), "INSUFFICIENT_STOCK", "NA"), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            log.error("Exception reserving stock", ex);
            return new ResponseEntity<>(ApiResponse.failure("Failed to reserve stock", HttpStatus.INTERNAL_SERVER_ERROR.value(), "INVENTORY_RESERVE_EXCEPTION", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/release/{productId}")
    public ResponseEntity<ApiResponse<InventoryDTO>> releaseStock(
            @PathVariable String productId,
            @RequestParam int quantity) {
        log.info("POST request to release {} units of productId: {}", quantity, productId);
        try {
            InventoryDTO updatedInventory = inventoryService.releaseStock(productId, quantity);
            if (updatedInventory != null) {
                return ResponseEntity.ok(ApiResponse.success("Stock released", HttpStatus.OK.value(), updatedInventory));
            }
            return new ResponseEntity<>(ApiResponse.failure("Failed to release stock", HttpStatus.BAD_REQUEST.value(), "INVENTORY_RELEASE_ERROR", "NA"), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            log.error("Exception releasing stock", ex);
            return new ResponseEntity<>(ApiResponse.failure("Failed to release stock", HttpStatus.INTERNAL_SERVER_ERROR.value(), "INVENTORY_RELEASE_EXCEPTION", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<ApiResponse<InventoryDTO>> addStock(
            @PathVariable String productId,
            @RequestParam int quantity) {
        log.info("POST request to add {} units to productId: {}", quantity, productId);
        try {
            InventoryDTO updatedInventory = inventoryService.addStock(productId, quantity);
            if (updatedInventory != null) {
                return ResponseEntity.ok(ApiResponse.success("Stock added", HttpStatus.OK.value(), updatedInventory));
            }
            return new ResponseEntity<>(ApiResponse.failure("Failed to add stock", HttpStatus.NOT_FOUND.value(), "INVENTORY_ADD_NOT_FOUND", "NA"), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            log.error("Exception adding stock", ex);
            return new ResponseEntity<>(ApiResponse.failure("Failed to add stock", HttpStatus.INTERNAL_SERVER_ERROR.value(), "INVENTORY_ADD_EXCEPTION", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/stats/low-stock-count")
    public ResponseEntity<ApiResponse<Long>> countLowStockItems() {
        log.info("GET request for low stock count");
        try {
            long count = inventoryService.countLowStockItems();
            return ResponseEntity.ok(ApiResponse.success("Low stock count fetched", HttpStatus.OK.value(), count));
        } catch (Exception ex) {
            log.error("Exception counting low stock items", ex);
            return new ResponseEntity<>(ApiResponse.failure("Failed to fetch low stock count", HttpStatus.INTERNAL_SERVER_ERROR.value(), "INVENTORY_STATS_EXCEPTION", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
