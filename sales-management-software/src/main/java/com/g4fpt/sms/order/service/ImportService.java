package com.g4fpt.sms.order.service;

import com.g4fpt.sms.order.dto.ImportDetailResponse;
import com.g4fpt.sms.order.dto.ImportRequest;
import com.g4fpt.sms.order.dto.ImportResponse;

import java.util.List;

public interface ImportService {
    List<ImportResponse> getAllImports(String status, String keyword);

    ImportResponse getImportById(Long id);

    List<ImportDetailResponse> getImportDetails(Long id);

    void createImportRequest(ImportRequest request, Long employeeId);

    void approveImportRequest(Long id);

    void rejectImportRequest(Long id, String reason);
}
