package com.g4fpt.sms.report.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import com.g4fpt.sms.report.dto.EmployeeSalesDTO;
import java.util.List;

import com.g4fpt.sms.order.entity.OrderTransaction;

public interface ReportService {
    
    Map<String, Object> getProfitReport(Long branchId, LocalDateTime startDate, LocalDateTime endDate);

    List<EmployeeSalesDTO> getEmployeeSalesReport(Long branchId, LocalDateTime startDate, LocalDateTime endDate);

    List<OrderTransaction> getEmployeeSalesDetails(Long employeeId, Long branchId, LocalDateTime startDate, LocalDateTime endDate);
    
}
