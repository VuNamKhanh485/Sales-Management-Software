package com.g4fpt.sms.order.service;

import com.g4fpt.sms.order.entity.OrderTransaction;
import com.g4fpt.sms.order.entity.ReturnRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReturnRequestService {
    OrderTransaction searchOrderByCode(String code);

    ReturnRequest createReturnRequest(Long orderId, Long branchId, Long requestedBy,
                                      String reason, List<ReturnItemInput> items,
                                      List<MultipartFile> images);

    List<ReturnRequest> getAllRequests();

    List<ReturnRequest> getPendingRequests();


    long countPendingRequests();


    ReturnRequest getById(Long id);

   
    void approveRequest(Long requestId, Long reviewerId);


    void rejectRequest(Long requestId, Long reviewerId, String reason);

  
    record ReturnItemInput(Long orderDetailId, Integer quantity) {}
}
