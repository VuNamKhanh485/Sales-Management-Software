package com.g4fpt.sms.order.service;

import com.g4fpt.sms.order.entity.OrderTransaction;
import com.g4fpt.sms.order.entity.ReturnRequest;

import java.util.List;

public interface ReturnRequestService {
    /** Tìm đơn hàng theo mã (code) kèm chi tiết */
    OrderTransaction searchOrderByCode(String code);

    /** Tạo yêu cầu trả hàng mới */
    ReturnRequest createReturnRequest(Long orderId, Long branchId, Long requestedBy,
                                      String reason, List<ReturnItemInput> items,
                                      List<String> imageUrls);

    /** Lấy tất cả yêu cầu */
    List<ReturnRequest> getAllRequests();

    /** Lấy yêu cầu chờ duyệt */
    List<ReturnRequest> getPendingRequests();

    /** Đếm số yêu cầu chờ duyệt */
    long countPendingRequests();

    /** Lấy chi tiết yêu cầu */
    ReturnRequest getById(Long id);

    /** Duyệt yêu cầu: +tồn kho, -điểm, -revenue */
    void approveRequest(Long requestId, Long reviewerId);

    /** Từ chối yêu cầu */
    void rejectRequest(Long requestId, Long reviewerId, String reason);

    /** Input cho 1 item trong yêu cầu trả */
    record ReturnItemInput(Long orderDetailId, Integer quantity) {}
}
