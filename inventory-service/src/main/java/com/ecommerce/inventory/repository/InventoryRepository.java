package com.ecommerce.inventory.repository;

import com.ecommerce.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    Optional<Inventory> findByProductId(String productId);

    @Query("SELECT i FROM Inventory i WHERE i.availableQuantity < i.reorderLevel")
    List<Inventory> findLowStockItems();

    @Query("SELECT i FROM Inventory i WHERE i.warehouse = :warehouse")
    List<Inventory> findByWarehouse(@Param("warehouse") String warehouse);

    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.availableQuantity < i.reorderLevel")
    long countLowStockItems();
}

