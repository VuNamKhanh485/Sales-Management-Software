package com.g4fpt.sms.payment.repository;

import com.g4fpt.sms.payment.entity.CashbookTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CashbookTransactionRepository extends JpaRepository<CashbookTransaction, Long> {

    Page<CashbookTransaction> findByBranchIdAndTransactionTypeAndPaymentMethod(Long branchId, String transactionType,
            String paymentMethod, Pageable pageable);

    Page<CashbookTransaction> findByBranchIdAndTransactionType(Long branchId, String transactionType,
            Pageable pageable);

    Page<CashbookTransaction> findByBranchIdAndPaymentMethod(Long branchId, String paymentMethod, Pageable pageable);

    Page<CashbookTransaction> findByBranchId(Long branchId, Pageable pageable);

    Page<CashbookTransaction> findByTransactionTypeAndPaymentMethod(String transactionType, String paymentMethod,
            Pageable pageable);

    Page<CashbookTransaction> findByTransactionType(String transactionType, Pageable pageable);

    Page<CashbookTransaction> findByPaymentMethod(String paymentMethod, Pageable pageable);

    List<CashbookTransaction> findByBranchIdAndTransactionTypeAndPaymentMethod(Long branchId, String transactionType,
            String paymentMethod);

    List<CashbookTransaction> findByTransactionTypeAndPaymentMethod(String transactionType, String paymentMethod);

    List<CashbookTransaction> findByBranchIdAndCreatedAtBetweenOrderByCreatedAtAsc(Long branchId,
            LocalDateTime startDate, LocalDateTime endDate);

    List<CashbookTransaction> findByCreatedAtBetweenOrderByCreatedAtAsc(LocalDateTime startDate, LocalDateTime endDate);

    List<CashbookTransaction> findByStatusOrderByCreatedAtDesc(String status);

    List<CashbookTransaction> findByBranchIdAndStatusOrderByCreatedAtDesc(Long branchId, String status);

}
