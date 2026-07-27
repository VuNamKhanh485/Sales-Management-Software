package com.g4fpt.sms.report.repository;

import com.g4fpt.sms.report.emuns.SnapshotType;
import com.g4fpt.sms.report.entity.InventorySnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InventorySnapshotRepository
                extends JpaRepository<InventorySnapshot, Long> {

        boolean existsBySnapshotTypeAndSnapshotDate(
                        SnapshotType snapshotType,
                        LocalDate snapshotDate);

        @Query("""
                            SELECT s
                            FROM InventorySnapshot s
                            WHERE s.snapshotType = :snapshotType
                              AND s.snapshotDate = (
                                    SELECT MAX(i.snapshotDate)
                                    FROM InventorySnapshot i
                                    WHERE i.snapshotType = :snapshotType
                                      AND i.snapshotDate <= :snapshotDate
                              )
                              AND (:branchId IS NULL OR s.branchId = :branchId)
                        """)
        List<InventorySnapshot> findLatestSnapshots(
                        @Param("snapshotType") SnapshotType snapshotType,
                        @Param("snapshotDate") LocalDate snapshotDate,
                        @Param("branchId") Long branchId);

        @Query("""
                            SELECT s
                            FROM InventorySnapshot s
                            WHERE s.snapshotType = :type
                              AND s.snapshotDate = :snapshotDate
                              AND (:branchId IS NULL OR s.branchId = :branchId)
                              AND (:categoryId IS NULL OR s.productUnit.product.category.id = :categoryId)
                              AND (:brandId IS NULL OR s.productUnit.product.brand.id = :brandId)
                              AND (
                                    :keyword IS NULL
                                    OR LOWER(s.productUnit.product.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                                    OR LOWER(s.productUnit.sku) LIKE LOWER(CONCAT('%', :keyword, '%'))
                              )
                            ORDER BY s.productUnit.product.name
                        """)
        List<InventorySnapshot> findSnapshotReport(
                        @Param("type") SnapshotType type,
                        @Param("snapshotDate") LocalDate snapshotDate,
                        @Param("branchId") Long branchId,
                        @Param("categoryId") Long categoryId,
                        @Param("brandId") Long brandId,
                        @Param("keyword") String keyword);

}
