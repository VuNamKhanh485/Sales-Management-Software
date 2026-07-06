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

    @Query(value = "SELECT * FROM OrderTransaction WHERE transaction_type = 'IMPORT' " +
           "AND (:status IS NULL OR :status = '' OR status = :status) " +
           "AND (:keyword IS NULL OR :keyword = '' OR LOWER(code) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY created_at DESC", nativeQuery = true)
    List<OrderTransaction> searchImports(@Param("status") String status, @Param("keyword") String keyword);
}