package com.g4fpt.sms.report.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

import com.g4fpt.sms.report.dto.EmployeeSalesDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.g4fpt.sms.order.entity.OrderTransaction;

public interface ReportService {
    
    Map<String, Object> getProfitReport(Long branchId, LocalDateTime startDate, LocalDateTime endDate);

    List<com.g4fpt.sms.report.dto.CashflowDetailDTO> getDetailedCashflow(Long branchId, LocalDateTime startDate, LocalDateTime endDate);

    List<EmployeeSalesDTO> getEmployeeSalesReport(Long branchId, LocalDateTime startDate, LocalDateTime endDate);

    List<OrderTransaction> getEmployeeSalesDetails(Long employeeId, Long branchId, LocalDateTime startDate, LocalDateTime endDate);
    
    Page<com.g4fpt.sms.report.dto.EmployeeOrderSalesDTO> getDetailedOrderSalesPage(Long branchId, Long employeeId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<com.g4fpt.sms.report.dto.EmployeeOrderSalesDTO> getDetailedOrderSalesList(Long branchId, Long employeeId, LocalDateTime startDate, LocalDateTime endDate);

    List<Object[]> getDetailedOrderSalesTotals(Long branchId, Long employeeId, LocalDateTime startDate, LocalDateTime endDate);

    byte[] exportDetailedOrderSalesToExcel(List<com.g4fpt.sms.report.dto.EmployeeOrderSalesDTO> data) throws java.io.IOException;
}
