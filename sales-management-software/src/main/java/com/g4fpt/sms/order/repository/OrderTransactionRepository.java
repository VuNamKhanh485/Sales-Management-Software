package com.g4fpt.sms.order.repository;
 
import com.g4fpt.sms.order.entity.OrderTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.time.LocalDateTime;
import java.util.List;
 
@Repository
public interface OrderTransactionRepository extends JpaRepository<OrderTransaction, Long> {
 
    Page<OrderTransaction> findByCustomerIdOrderByCreatedAtDesc(
            Long customerId, Pageable pageable);
 
    long countByCustomerId(Long customerId);
 
    @EntityGraph(attributePaths = {"customer"})
    List<OrderTransaction> findByCreatedByAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long createdBy, LocalDateTime startDate, LocalDateTime endDate);
 
    @EntityGraph(attributePaths = {"customer"})
    List<OrderTransaction> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime startDate, LocalDateTime endDate);
}