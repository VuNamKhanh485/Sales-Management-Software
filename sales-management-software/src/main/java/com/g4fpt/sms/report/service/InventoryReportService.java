package com.g4fpt.sms.report.service;

import com.g4fpt.sms.report.dto.InventoryReportDTO;
import com.g4fpt.sms.report.dto.InventoryReportFilterRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InventoryReportService {
    public List<InventoryReportDTO> generateReport(InventoryReportFilterRequest filter);
}
