package com.g4fpt.sms.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImportResponse {
    private Long id;
    private String code;
    private String branchName;
    private String supplierName;
    private String creatorName;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private String note;
}
