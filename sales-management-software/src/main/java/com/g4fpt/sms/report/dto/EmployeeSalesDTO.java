package com.g4fpt.sms.report.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSalesDTO {
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private Long totalOrders;
    private BigDecimal totalSales;
}
