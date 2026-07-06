package com.g4fpt.sms.order.service.impl;

import com.g4fpt.sms.customer.entity.Customer;
import com.g4fpt.sms.customer.repository.CustomerRepository;
import com.g4fpt.sms.inventory.entity.Inventory;
import com.g4fpt.sms.inventory.repository.InventoryRepository;
import com.g4fpt.sms.order.entity.*;
import com.g4fpt.sms.order.repository.OrderTransactionRepository;
import com.g4fpt.sms.order.repository.ReturnRequestRepository;
import com.g4fpt.sms.order.service.ReturnRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReturnRequestServiceImpl implements ReturnRequestService {

    private final ReturnRequestRepository returnRequestRepository;
    private final OrderTransactionRepository orderTransactionRepository;
    private final InventoryRepository inventoryRepository;
    private final CustomerRepository customerRepository;

    @Override
    public OrderTransaction searchOrderByCode(String code) {
        return orderTransactionRepository.findByCodeWithDetails(code)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với mã: " + code));
    }

    @Override
    @Transactional
    public ReturnRequest createReturnRequest(Long orderId, Long branchId, Long requestedBy,
                                              String reason, List<ReturnItemInput> items,
                                              List<String> imageUrls) {
        OrderTransaction order = orderTransactionRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        ReturnRequest request = ReturnRequest.builder()
                .order(order)
                .branchId(branchId)
                .requestedBy(requestedBy)
                .reason(reason)
                .status("PENDING")
                .build();

        for (ReturnItemInput input : items) {
            OrderTransactionDetail detail = order.getDetails().stream()
                    .filter(d -> d.getId().equals(input.orderDetailId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Chi tiết đơn hàng không hợp lệ"));

            if (input.quantity() <= 0 || input.quantity() > detail.getQuantity()) {
                throw new RuntimeException("Số lượng trả không hợp lệ cho sản phẩm: "
                        + detail.getProductUnit().getProduct().getName());
            }

            ReturnRequestItem rri = ReturnRequestItem.builder()
                    .returnRequest(request)
                    .orderDetail(detail)
                    .productUnit(detail.getProductUnit())
                    .quantity(input.quantity())
                    .salePrice(detail.getSalePrice())
                    .build();
            request.getItems().add(rri);
        }

        // Ảnh (tùy chọn)
        if (imageUrls != null) {
            for (String url : imageUrls) {
                ReturnRequestImage img = ReturnRequestImage.builder()
                        .returnRequest(request)
                        .imageUrl(url)
                        .build();
                request.getImages().add(img);
            }
        }

        return returnRequestRepository.save(request);
    }

    @Override
    public List<ReturnRequest> getAllRequests() {
        return returnRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public List<ReturnRequest> getPendingRequests() {
        return returnRequestRepository.findByStatusOrderByCreatedAtDesc("PENDING");
    }

    @Override
    public long countPendingRequests() {
        return returnRequestRepository.countByStatus("PENDING");
    }

    @Override
    public ReturnRequest getById(Long id) {
        return returnRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu trả hàng"));
    }

    @Override
    @Transactional
    public void approveRequest(Long requestId, Long reviewerId) {
        ReturnRequest req = returnRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu trả hàng"));

        if (!"PENDING".equals(req.getStatus())) {
            throw new RuntimeException("Yêu cầu đã được xử lý trước đó");
        }

        OrderTransaction order = req.getOrder();
        Long branchId = order.getBranchId();

        for (ReturnRequestItem item : req.getItems()) {
            // + tồn kho
            Inventory inv = inventoryRepository.findByBranchIdAndProductUnitId(
                            branchId, item.getProductUnit().getId())
                    .orElse(null);
            if (inv != null) {
                inv.setStock(inv.getStock() + item.getQuantity());
            }

            // trừ doanh thu & điểm của khách hàng
            if (order.getCustomer() != null) {
                Customer customer = order.getCustomer();
                BigDecimal refundAmount = item.getSalePrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()));

                customer.setTotalRevenue(customer.getTotalRevenue()
                        .subtract(refundAmount).max(BigDecimal.ZERO));

                int pointToReverse = refundAmount
                        .divide(new BigDecimal("10000"), 0, RoundingMode.FLOOR).intValue();
                customer.setTotalPoint(Math.max(0, customer.getTotalPoint() - pointToReverse));
            }
        }

        req.setStatus("APPROVED");
        req.setReviewedBy(reviewerId);
        req.setReviewedAt(LocalDateTime.now());

        // Đánh dấu đơn hàng là RETURNED
        order.setStatus("RETURNED");
    }

    @Override
    @Transactional
    public void rejectRequest(Long requestId, Long reviewerId, String reason) {
        ReturnRequest req = returnRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu trả hàng"));

        if (!"PENDING".equals(req.getStatus())) {
            throw new RuntimeException("Yêu cầu đã được xử lý trước đó");
        }

        req.setStatus("REJECTED");
        req.setReviewedBy(reviewerId);
        req.setReviewedAt(LocalDateTime.now());
        req.setRejectReason(reason);
    }
}
