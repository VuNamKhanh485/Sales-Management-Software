package com.g4fpt.sms.payment.service;

import com.g4fpt.sms.payment.dto.CashbookDTO;
import com.g4fpt.sms.payment.entity.CashbookTransaction;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface CashbookService {

    Page<CashbookTransaction> getTransactions(Long branchId, String type, String method, 
                                              LocalDateTime startDate, LocalDateTime endDate, 
                                              int page, int size);

    CashbookTransaction getTransactionById(Long id);

    CashbookTransaction createTransaction(CashbookDTO dto, Long createdBy);

    void approveTransaction(Long id);

    void rejectTransaction(Long id);

    java.util.List<CashbookTransaction> getPendingTransactions(Long branchId);

    BigDecimal getBalance(Long branchId, String method);

    byte[] exportExcel(Long branchId, String type, String method, LocalDateTime startDate, LocalDateTime endDate);
}
