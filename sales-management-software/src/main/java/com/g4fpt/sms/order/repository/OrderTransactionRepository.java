package com.g4fpt.sms.order.repository;
 
import com.g4fpt.sms.order.entity.OrderTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface OrderTransactionRepository extends JpaRepository<OrderTransaction, Long> {
 
    Page<OrderTransaction> findByCustomerIdOrderByCreatedAtDesc(
            Long customerId, Pageable pageable);
    long countByCustomerId(Long customerId);

    boolean existsByVoucherId(Long voucherId);


    @Query(value = "SELECT * FROM OrderTransaction WHERE created_by = :createdBy AND created_at BETWEEN :start AND :end ORDER BY created_at DESC", nativeQuery = true)
    List<OrderTransaction> findByCreatedByAndDateRange(
            @Param("createdBy") Long createdBy,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query(value = "SELECT * FROM OrderTransaction WHERE created_at BETWEEN :start AND :end ORDER BY created_at DESC", nativeQuery = true)
    List<OrderTransaction> findByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT o FROM OrderTransaction o LEFT JOIN FETCH o.details d LEFT JOIN FETCH d.productUnit pu LEFT JOIN FETCH pu.product WHERE o.code = :code")
    Optional<OrderTransaction> findByCodeWithDetails(@Param("code") String code);

    @Query("SELECT o FROM OrderTransaction o LEFT JOIN FETCH o.details d LEFT JOIN FETCH d.productUnit pu LEFT JOIN FETCH pu.product WHERE o.id = :id")
    Optional<OrderTransaction> findByIdWithDetails(@Param("id") Long id);

    @Query(value = "SELECT * FROM OrderTransaction " +
            "WHERE transaction_type = 'IMPORT' " +
            "AND (:status IS NULL OR status = :status) " +
            "AND (:keyword IS NULL OR code LIKE %:keyword% OR note LIKE %:keyword%) " +
            "ORDER BY created_at DESC",
            nativeQuery = true)
    List<OrderTransaction> searchImports(
            @Param("status") String status,
            @Param("keyword") String keyword);

    @Query("SELECT o FROM OrderTransaction o WHERE " +
           "(:branchId IS NULL OR o.branchId = :branchId) AND " +
           "o.status = 'COMPLETED' AND " +
           "o.createdAt >= :startDate AND o.createdAt <= :endDate " +
           "ORDER BY o.createdAt ASC")
    List<OrderTransaction> findCompletedTransactionsForReport(
            @Param("branchId") Long branchId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new com.g4fpt.sms.report.dto.EmployeeSalesDTO(e.id, e.employeeCode, e.fullName, COUNT(o), SUM(o.finalAmount)) " +
           "FROM OrderTransaction o JOIN Employee e ON o.createdBy = e.id " +
           "WHERE (:branchId IS NULL OR o.branchId = :branchId) AND " +
           "o.transactionType = 'SALE' AND o.status = 'COMPLETED' AND " +
           "o.createdAt >= :startDate AND o.createdAt <= :endDate " +
           "GROUP BY e.id, e.employeeCode, e.fullName " +
           "ORDER BY SUM(o.finalAmount) DESC")
    List<com.g4fpt.sms.report.dto.EmployeeSalesDTO> getEmployeeSalesReport(
            @Param("branchId") Long branchId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM OrderTransaction o WHERE " +
           "o.createdBy = :employeeId AND " +
           "(:branchId IS NULL OR o.branchId = :branchId) AND " +
           "o.transactionType = 'SALE' AND o.status = 'COMPLETED' AND " +
           "o.createdAt >= :startDate AND o.createdAt <= :endDate " +
           "ORDER BY o.createdAt DESC")
    List<OrderTransaction> findEmployeeSalesDetails(
            @Param("employeeId") Long employeeId,
            @Param("branchId") Long branchId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}