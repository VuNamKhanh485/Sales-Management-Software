package com.g4fpt.sms.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashflowDetailDTO {
    private LocalDateTime createdAt;
    private String code;
    private String type;
    private String description;
    private BigDecimal amountIn;
    private BigDecimal amountOut;
    private Long referenceId;
    private String transactionUrl;
}
