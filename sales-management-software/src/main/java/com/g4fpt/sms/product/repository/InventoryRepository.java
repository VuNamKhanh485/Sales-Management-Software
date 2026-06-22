package com.g4fpt.sms.product.repository;

import com.g4fpt.sms.product.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByBranchIdAndProductUnitId(Long branchId, Long productUnitId);

    @Query("SELECT i FROM Inventory i WHERE " +
           "(:branchId IS NULL OR i.branch.id = :branchId) AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           " LOWER(i.productUnit.product.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(i.productUnit.sku) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Inventory> findAllFiltered(
        @Param("branchId") Long branchId, 
        @Param("keyword") String keyword, 
        Pageable pageable
    );
}
