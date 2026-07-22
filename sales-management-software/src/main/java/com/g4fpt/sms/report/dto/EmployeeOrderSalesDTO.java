package com.g4fpt.sms.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeOrderSalesDTO {
    private Long orderId;
    private String orderCode;
    private LocalDateTime createdAt;
    private String branchName;
    private String employeeName;
    private BigDecimal totalAmount;
}
