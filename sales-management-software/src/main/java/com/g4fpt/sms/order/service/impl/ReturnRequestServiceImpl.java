package com.g4fpt.sms.order.service.impl;

import com.g4fpt.sms.customer.entity.Customer;
import com.g4fpt.sms.customer.repository.CustomerRepository;
import com.g4fpt.sms.inventory.entity.Inventory;
import com.g4fpt.sms.inventory.repository.InventoryRepository;
import com.g4fpt.sms.order.entity.*;
import com.g4fpt.sms.order.repository.OrderTransactionRepository;
import com.g4fpt.sms.order.repository.ReturnRequestRepository;
import com.g4fpt.sms.order.service.ReturnRequestService;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.service.FileStorageService;
import com.g4fpt.sms.common.enums.UploadFolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ReturnRequestServiceImpl implements ReturnRequestService {

    private final ReturnRequestRepository returnRequestRepository;
    private final OrderTransactionRepository orderTransactionRepository;
    private final InventoryRepository inventoryRepository;
    private final CustomerRepository customerRepository;
    private final FileStorageService fileStorageService;

    // Tìm kiếm đơn hàng để trả hàng
    @Override
    public OrderTransaction searchOrderByCode(String code) {
        OrderTransaction order = orderTransactionRepository.findByCodeWithDetails(code).orElse(null);
        if (order == null) {
            throw new RuntimeException("Không tìm thấy đơn hàng mã " + code);
        }

        if (!"COMPLETED".equals(order.getStatus())) {
            String statusVn = order.getStatus().equals("REFUNDED") ? "Đã hoàn tiền"
                    : (order.getStatus().equals("CANCELLED") ? "Đã hủy"
                            : (order.getStatus().equals("RETURNED") ? "Đã bị hoàn"
                                    : (order.getStatus().equals("PENDING") ? "Chờ xử lý" : order.getStatus())));
            throw new RuntimeException("Chỉ có thể trả các đơn hàng đã thanh toán. Trạng thái hiện tại: " + statusVn);
        }

        if (!"SALE".equals(order.getTransactionType())) {
            String typeVn = order.getTransactionType().equals("IMPORT") ? "Nhập hàng"
                    : (order.getTransactionType().equals("TRANSFER") ? "Chuyển kho"
                            : (order.getTransactionType().equals("RETURN") ? "Trả hàng" : order.getTransactionType()));
            throw new RuntimeException("Chỉ có thể trả các đơn hàng bán lẻ. Loại giao dịch hiện tại: " + typeVn);
        }

        return order;
    }

    // Tạo yêu cầu trả hàng
    @Override
    @Transactional
    public ReturnRequest createReturnRequest(Long orderId, Long branchId, Long requestedBy,
            String reason, List<ReturnItemInput> items,
            List<MultipartFile> images) {
        if (returnRequestRepository.existsByOrderIdAndStatus(orderId, "PENDING")) {
            throw new RuntimeException("Đơn hàng này đang có yêu cầu trả hàng chờ duyệt. Không thể tạo thêm yêu cầu mới!");
        }
        OrderTransaction order = orderTransactionRepository.findByIdWithDetails(orderId).orElse(null);
        if (order == null) {
            throw new RuntimeException("Không tìm thấy đơn hàng");
        }

        ReturnRequest request = ReturnRequest.builder()
                .order(order)
                .branchId(branchId)
                .requestedBy(requestedBy)
                .reason(reason)
                .status("PENDING")
                .build();

        for (ReturnItemInput input : items) {
            OrderTransactionDetail detail = null;
            // Dùng vòng lặp for thay cho Stream.filter().findFirst()
            for (OrderTransactionDetail d : order.getDetails()) {
                if (d.getId().equals(input.orderDetailId())) {
                    detail = d;
                    break;
                }
            }

            if (detail == null) {
                throw new RuntimeException("Chi tiết đơn hàng không hợp lệ");
            }

            if (input.quantity() <= 0 || input.quantity() > detail.getQuantity()) {
                String productName = "Không tên";
                if (detail.getProductUnit() != null && detail.getProductUnit().getProduct() != null) {
                    productName = detail.getProductUnit().getProduct().getName();
                }
                throw new RuntimeException("Số lượng trả không hợp lệ cho sản phẩm: " + productName);
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

        // Lưu ảnh minh chứng (tùy chọn)
        if (images != null) {
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    try {
                        String fileName = fileStorageService.saveFile(file, UploadFolder.RETURN);
                        ReturnRequestImage img = ReturnRequestImage.builder()
                                .returnRequest(request)
                                .imageUrl(fileName)
                                .build();
                        request.getImages().add(img);
                    } catch (Exception e) {
                        throw new RuntimeException("Lỗi lưu file ảnh trả hàng: " + e.getMessage(), e);
                    }
                }
            }
        }

        return returnRequestRepository.save(request);
    }

    // Lấy toàn bộ danh sách yêu cầu
    @Override
    public List<ReturnRequest> getAllRequests() {
        return returnRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    // Lấy yêu cầu đang chờ duyệt
    @Override
    public List<ReturnRequest> getPendingRequests() {
        return returnRequestRepository.findByStatusOrderByCreatedAtDesc("PENDING");
    }

    // Đếm số lượng chờ duyệt
    @Override
    public long countPendingRequests() {
        return returnRequestRepository.countByStatus("PENDING");
    }

    // Xem chi tiết yêu cầu
    @Override
    public ReturnRequest getById(Long id) {
        ReturnRequest req = returnRequestRepository.findById(id).orElse(null);
        if (req == null) {
            throw new RuntimeException("Không tìm thấy yêu cầu trả hàng");
        }
        return req;
    }

    // Xử lý duyệt yêu cầu
    @Override
    @Transactional
    public void approveRequest(Long requestId, Long reviewerId) {
        ReturnRequest req = returnRequestRepository.findById(requestId).orElse(null);
        if (req == null) {
            throw new RuntimeException("Không tìm thấy yêu cầu trả hàng");
        }

        if (!"PENDING".equals(req.getStatus())) {
            throw new RuntimeException("Yêu cầu đã được xử lý trước đó");
        }

        OrderTransaction order = req.getOrder();
        if ("REFUNDED".equals(order.getStatus()) || "RETURNED".equals(order.getStatus())) {
            throw new RuntimeException("Đơn hàng này đã được duyệt trả hàng ở một yêu cầu khác");
        }

        Long branchId = order.getBranchId();

        for (ReturnRequestItem item : req.getItems()) {
            // Cộng lại tồn kho
            Inventory inv = inventoryRepository.findByBranchIdAndProductUnitId(
                    branchId, item.getProductUnit().getId())
                    .orElse(null);
            if (inv != null) {
                inv.setStock(inv.getStock() + item.getQuantity());
                inventoryRepository.save(inv);
            }

            // Trừ doanh thu & điểm của khách hàng
            if (order.getCustomer() != null) {
                Customer customer = customerRepository.findById(order.getCustomer().getId()).orElse(null);
                if (customer != null) {
                    BigDecimal refundAmount = item.getSalePrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));

                    BigDecimal currentRevenue = customer.getTotalRevenue();
                    if (currentRevenue == null) {
                        currentRevenue = BigDecimal.ZERO;
                    }
                    customer.setTotalRevenue(currentRevenue.subtract(refundAmount).max(BigDecimal.ZERO));

                    int currentPoint = 0;
                    if (customer.getTotalPoint() != null) {
                        currentPoint = customer.getTotalPoint();
                    }

                    int pointToReverse = refundAmount
                            .divide(new BigDecimal("10000"), 0, RoundingMode.FLOOR).intValue();
                    int newTotalPoint = Math.max(0, currentPoint - pointToReverse);

                    customer.setTotalPoint(newTotalPoint);

                    if (customer.getUsedPoint() != null && customer.getUsedPoint() > newTotalPoint) {
                        customer.setUsedPoint(newTotalPoint);
                    }

                    customerRepository.save(customer);
                }
            }
        }

        req.setStatus("APPROVED");
        req.setReviewedBy(reviewerId);
        req.setReviewedAt(LocalDateTime.now());
        returnRequestRepository.save(req);

        // Đánh dấu đơn hàng gốc là REFUNDED
        order.setStatus("REFUNDED");
        orderTransactionRepository.save(order);

        // Tạo giao dịch trả hàng ghi vào lịch sử
        BigDecimal totalRefund = BigDecimal.ZERO;
        for (ReturnRequestItem item : req.getItems()) {
            totalRefund = totalRefund.add(item.getSalePrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        OrderTransaction returnTx = OrderTransaction.builder()
                .branchId(branchId)
                .createdBy(reviewerId)
                .originalOrderId(order.getId())
                .customer(order.getCustomer())
                .code("RET-"
                        + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")))
                .totalAmount(totalRefund)
                .finalAmount(totalRefund)
                .paidAmount(totalRefund)
                .changeAmount(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .status("COMPLETED")
                .transactionType("RETURN")
                .paymentMethodId(order.getPaymentMethodId())
                .note("Giao dịch trả hàng từ mã đơn: " + order.getCode())
                .build();

        List<OrderTransactionDetail> detailList = new ArrayList<>();
        for (ReturnRequestItem item : req.getItems()) {
            OrderTransactionDetail detail = OrderTransactionDetail.builder()
                    .orderTransaction(returnTx)
                    .productUnit(item.getProductUnit())
                    .quantity(item.getQuantity())
                    .salePrice(item.getSalePrice())
                    .discountAmount(BigDecimal.ZERO)
                    .totalAmount(item.getSalePrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .build();
            detailList.add(detail);
        }
        returnTx.setDetails(detailList);

        orderTransactionRepository.save(returnTx);
    }

    // Từ chối yêu cầu trả hàng
    @Override
    @Transactional
    public void rejectRequest(Long requestId, Long reviewerId, String reason) {
        ReturnRequest req = returnRequestRepository.findById(requestId).orElse(null);
        if (req == null) {
            throw new RuntimeException("Không tìm thấy yêu cầu trả hàng");
        }

        if (!"PENDING".equals(req.getStatus())) {
            throw new RuntimeException("Yêu cầu đã được xử lý trước đó");
        }

        req.setStatus("REJECTED");
        req.setReviewedBy(reviewerId);
        req.setReviewedAt(LocalDateTime.now());
        req.setRejectReason(reason);
        returnRequestRepository.save(req);
    }
}
