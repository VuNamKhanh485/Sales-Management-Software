package com.g4fpt.sms.order.repository;

import com.g4fpt.sms.order.entity.OrderTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderTransactionRepository extends JpaRepository<OrderTransaction, Long> {

    Page<OrderTransaction> findByCustomerIdOrderByCreatedAtDesc(
            Long customerId, Pageable pageable);

    @Query("SELECT o FROM OrderTransaction o WHERE o.transactionType = 'IMPORT' " +
           "AND (:status IS NULL OR :status = '' OR o.status = :status) " +
           "AND (:keyword IS NULL OR :keyword = '' OR LOWER(o.code) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY o.createdAt DESC")
    List<OrderTransaction> searchImports(@Param("status") String status, @Param("keyword") String keyword);
}