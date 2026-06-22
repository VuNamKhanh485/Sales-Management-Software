package com.g4fpt.sms.product.repository;

import com.g4fpt.sms.product.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByBranchIdAndProductUnitId(Long branchId, Long productUnitId);
}
