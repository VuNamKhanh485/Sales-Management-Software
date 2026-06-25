package com.g4fpt.sms.order.repository;

import com.g4fpt.sms.order.entity.OrderTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderTransactionRepository extends JpaRepository<OrderTransaction, Long> {

    Page<OrderTransaction> findByCustomerIdOrderByCreatedAtDesc(
            Long customerId, Pageable pageable);

    long countByCustomerId(Long customerId);

    @Query("SELECT o FROM OrderTransaction o LEFT JOIN FETCH o.customer " +
           "WHERE o.createdBy = :createdBy " +
           "AND o.createdAt >= :startDate " +
           "AND o.createdAt <= :endDate " +
           "ORDER BY o.createdAt DESC")
    List<OrderTransaction> findByCreatedByAndDateRange(
            @Param("createdBy") Long createdBy,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM OrderTransaction o LEFT JOIN FETCH o.customer " +
           "WHERE o.createdAt >= :startDate " +
           "AND o.createdAt <= :endDate " +
           "ORDER BY o.createdAt DESC")
    List<OrderTransaction> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}