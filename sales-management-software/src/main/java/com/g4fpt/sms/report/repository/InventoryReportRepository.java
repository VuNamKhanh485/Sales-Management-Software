package com.g4fpt.sms.report.repository;

import com.g4fpt.sms.order.entity.OrderTransactionDetail;
import com.g4fpt.sms.report.dto.InventoryMovementProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface InventoryReportRepository extends JpaRepository<OrderTransactionDetail, Long> {

    @Query(value = """
        SELECT branch_id AS branchId, product_unit_id AS productUnitId,
               SUM(quantity) AS qty, SUM(total_amount) AS value
        FROM (
            SELECT ot.branch_id AS branch_id, otd.product_unit_id, otd.quantity, otd.total_amount
            FROM order_transaction ot
            JOIN order_transaction_detail otd ON otd.order_transaction_id = ot.id
            WHERE ot.transaction_type = 'IMPORT'
              AND ot.status = 'RECEIVED'
              AND ot.created_at BETWEEN :fromDate AND :toDate

            UNION ALL

            SELECT ot.branch_id AS branch_id, otd.product_unit_id, otd.quantity, otd.total_amount
            FROM order_transaction ot
            JOIN order_transaction_detail otd ON otd.order_transaction_id = ot.id
            WHERE ot.transaction_type = 'RETURN'
              AND ot.status = 'COMPLETED'
              AND ot.created_at BETWEEN :fromDate AND :toDate

            UNION ALL

            SELECT ot.to_branch_id AS branch_id, otd.product_unit_id, otd.quantity, otd.total_amount
            FROM order_transaction ot
            JOIN order_transaction_detail otd ON otd.order_transaction_id = ot.id
            WHERE ot.transaction_type = 'TRANSFER'
              AND ot.status = 'COMPLETED'
              AND ot.created_at BETWEEN :fromDate AND :toDate
        ) AS nhap
        WHERE (:branchId IS NULL OR branch_id = :branchId)
        GROUP BY branch_id, product_unit_id
        """, nativeQuery = true)
    List<InventoryMovementProjection> sumImportByPeriod(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("branchId") Long branchId
    );

    @Query(value = """
        SELECT branch_id AS branchId, product_unit_id AS productUnitId,
               SUM(quantity) AS qty, SUM(total_amount) AS value
        FROM (
            SELECT ot.branch_id AS branch_id, otd.product_unit_id, otd.quantity, otd.total_amount
            FROM order_transaction ot
            JOIN order_transaction_detail otd ON otd.order_transaction_id = ot.id
            WHERE ot.transaction_type = 'SALE'
              AND ot.status = 'COMPLETED'
              AND ot.created_at BETWEEN :fromDate AND :toDate

            UNION ALL

            SELECT ot.from_branch_id AS branch_id, otd.product_unit_id, otd.quantity, otd.total_amount
            FROM order_transaction ot
            JOIN order_transaction_detail otd ON otd.order_transaction_id = ot.id
            WHERE ot.transaction_type = 'TRANSFER'
              AND ot.status = 'COMPLETED'
              AND ot.created_at BETWEEN :fromDate AND :toDate
        ) AS xuat
        WHERE (:branchId IS NULL OR branch_id = :branchId)
        GROUP BY branch_id, product_unit_id
        """, nativeQuery = true)
    List<InventoryMovementProjection> sumExportByPeriod(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("branchId") Long branchId
    );
}
