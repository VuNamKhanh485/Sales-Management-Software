package com.g4fpt.sms.report.repository;

import com.g4fpt.sms.order.entity.OrderTransactionDetail;
import com.g4fpt.sms.report.projection.InventoryMovementProjection;
import com.g4fpt.sms.report.projection.LastImportPriceProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface InventoryReportRepository extends JpaRepository<OrderTransactionDetail, Long> {

    // ===== NHẬP trong khoảng thời gian =====
    @Query(value = """
        SELECT branch_id AS branchId, product_unit_id AS productUnitId,
               SUM(quantity) AS qty, SUM(total_amount) AS value
        FROM (
            SELECT ot.branch_id AS branch_id, otd.product_unit_id, otd.quantity, otd.total_amount
            FROM ordertransaction ot
            JOIN ordertransactiondetail otd ON otd.order_transaction_id = ot.id
            WHERE ot.transaction_type = 'IMPORT'
              AND ot.status = 'RECEIVED'
              AND ot.created_at BETWEEN :fromDate AND :toDate

            UNION ALL

            SELECT ot.branch_id AS branch_id, otd.product_unit_id, otd.quantity, otd.total_amount
            FROM ordertransaction ot
            JOIN ordertransactiondetail otd ON otd.order_transaction_id = ot.id
            WHERE ot.transaction_type = 'RETURN'
              AND ot.status = 'COMPLETED'
              AND ot.created_at BETWEEN :fromDate AND :toDate

            UNION ALL

            SELECT ot.to_branch_id AS branch_id, otd.product_unit_id, otd.quantity, otd.total_amount
            FROM ordertransaction ot
            JOIN ordertransactiondetail otd ON otd.order_transaction_id = ot.id
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

    // ===== XUẤT trong khoảng thời gian =====
    @Query(value = """
        SELECT branch_id AS branchId, product_unit_id AS productUnitId,
               SUM(quantity) AS qty, SUM(total_amount) AS value
        FROM (
            SELECT ot.branch_id AS branch_id, otd.product_unit_id, otd.quantity, otd.total_amount
            FROM ordertransaction ot
            JOIN ordertransactiondetail otd ON otd.order_transaction_id = ot.id
            WHERE ot.transaction_type = 'SALE'
              AND ot.status = 'COMPLETED'
              AND ot.created_at BETWEEN :fromDate AND :toDate

            UNION ALL

            SELECT ot.from_branch_id AS branch_id, otd.product_unit_id, otd.quantity, otd.total_amount
            FROM ordertransaction ot
            JOIN ordertransactiondetail otd ON otd.order_transaction_id = ot.id
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

    // ===== Giá nhập GẦN NHẤT của mỗi SKU tính đến 1 thời điểm (dùng để định giá tồn đầu/cuối kỳ) =====
    @Query(value = """
        SELECT otd.product_unit_id AS productUnitId, otd.import_price AS importPrice
        FROM ordertransactiondetail otd
        JOIN ordertransaction ot ON ot.id = otd.order_transaction_id
        WHERE ot.transaction_type = 'IMPORT'
          AND ot.status = 'RECEIVED'
          AND ot.created_at <= :atDate
          AND (:branchId IS NULL OR ot.branch_id = :branchId)
          AND otd.id = (
              SELECT otd2.id
              FROM ordertransactiondetail otd2
              JOIN ordertransaction ot2 ON ot2.id = otd2.order_transaction_id
              WHERE otd2.product_unit_id = otd.product_unit_id
                AND ot2.transaction_type = 'IMPORT'
                AND ot2.status = 'RECEIVED'
                AND ot2.created_at <= :atDate
                AND (:branchId IS NULL OR ot2.branch_id = :branchId)
              ORDER BY ot2.created_at DESC
              LIMIT 1
          )
        """, nativeQuery = true)
    List<LastImportPriceProjection> findLastImportPrices(
            @Param("atDate") LocalDateTime atDate,
            @Param("branchId") Long branchId
    );
}
