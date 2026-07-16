package com.g4fpt.sms.payment.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CashbookRequestDTO {
    private Long branchId;
    private String transactionType; // IN / OUT
    private String paymentMethod; // CASH / BANK
    private BigDecimal amount;
    private String referenceCode;
    private String description;
}
