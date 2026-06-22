package com.g4fpt.sms.inventory.repository;

import com.g4fpt.sms.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByBranchId(Long branchId);

    Optional<Inventory> findByBranchIdAndProductUnitId(
            Long branchId,
            Long productUnitId
    );

    boolean existsByBranchIdAndProductUnitId(
            Long branchId,
            Long productUnitId
    );
}