package com.g4fpt.sms.order.service;

import com.g4fpt.sms.order.dto.TransferRequest;
import com.g4fpt.sms.order.dto.TransferResponse;
import com.g4fpt.sms.order.dto.TransferDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransferService {

    // Tạo yêu cầu chuyển kho mới
    void createTransferRequest(TransferRequest request, Long creatorId);

    // Duyệt yêu cầu chuyển kho
    void approveTransferRequest(Long id, Long approverId);

    // Từ chối yêu cầu chuyển kho
    void rejectTransferRequest(Long id, Long rejectorId, String reason);

    // Lấy danh sách phiếu chuyển kho có phân trang (có thể lọc theo kho xuất, kho nhận, hoặc cả hai)
    Page<TransferResponse> getTransfers(Long fromBranchId, Long toBranchId, String status, Long userBranchId, Pageable pageable);

    // Lấy thông tin một phiếu chuyển kho cụ thể theo id
    TransferResponse getTransferById(Long id);

    // Lấy chi tiết các sản phẩm trong một phiếu chuyển kho
    List<TransferDetailResponse> getTransferDetails(Long transactionId);
}
