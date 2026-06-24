package com.g4fpt.sms.order.repository;

import com.g4fpt.sms.order.entity.OrderTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderTransactionRepository extends JpaRepository<OrderTransaction, Long> {

    Page<OrderTransaction> findByCustomerIdOrderByCreatedAtDesc(
            Long customerId, Pageable pageable);


}