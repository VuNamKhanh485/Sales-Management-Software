package com.g4fpt.sms.report.service;

import com.g4fpt.sms.report.dto.InventoryReportDTO;
import com.g4fpt.sms.report.dto.InventoryReportFilterRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface InventoryReportService {
    List<InventoryReportDTO> generateReport(InventoryReportFilterRequest filter);
    void exportExcel(InventoryReportFilterRequest filter, HttpServletResponse response) throws IOException;
}
