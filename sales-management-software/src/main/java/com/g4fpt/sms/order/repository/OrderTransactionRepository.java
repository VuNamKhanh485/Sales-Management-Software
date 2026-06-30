package com.g4fpt.sms.order.repository;
 
import com.g4fpt.sms.order.entity.OrderTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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
 
    boolean existsByVoucherId(Long voucherId);
 
    @EntityGraph(attributePaths = {"customer"})
    List<OrderTransaction> findByCreatedByAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long createdBy, LocalDateTime startDate, LocalDateTime endDate);
 
    @Query("SELECT o FROM OrderTransaction o LEFT JOIN FETCH o.customer WHERE o.createdAt BETWEEN :start AND :end ORDER BY o.createdAt DESC")
    List<OrderTransaction> findByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}