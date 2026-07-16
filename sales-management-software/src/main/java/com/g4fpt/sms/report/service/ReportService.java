package com.g4fpt.sms.report.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public interface ReportService {
    
    Map<String, Object> getProfitReport(Long branchId, LocalDateTime startDate, LocalDateTime endDate);
    
}
