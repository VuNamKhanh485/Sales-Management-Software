package com.g4fpt.sms.order.service.impl;

import com.g4fpt.sms.branch.entity.Branch;
import com.g4fpt.sms.branch.repository.BranchRepository;
import com.g4fpt.sms.employee.entity.Employee;
import com.g4fpt.sms.employee.repository.EmployeeRepository;
import com.g4fpt.sms.inventory.entity.Inventory;
import com.g4fpt.sms.inventory.repository.InventoryRepository;
import com.g4fpt.sms.order.dto.TransferDetailResponse;
import com.g4fpt.sms.order.dto.TransferItemRequest;
import com.g4fpt.sms.order.dto.TransferRequest;
import com.g4fpt.sms.order.dto.TransferResponse;
import com.g4fpt.sms.order.entity.OrderTransaction;
import com.g4fpt.sms.order.entity.OrderTransactionDetail;
import com.g4fpt.sms.order.repository.OrderTransactionRepository;
import com.g4fpt.sms.order.service.TransferService;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.repository.ProductUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final OrderTransactionRepository orderTransactionRepository;
    private final BranchRepository branchRepository;
    private final EmployeeRepository employeeRepository;
    private final ProductUnitRepository productUnitRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public void createTransferRequest(TransferRequest request, Long creatorId) {
        if (request.getFromBranchId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn chi nhánh xuất hàng!");
        }
        if (request.getToBranchId() == null) {
            throw new IllegalArgumentException("Chi nhánh nhận không hợp lệ!");
        }
        if (request.getFromBranchId().equals(request.getToBranchId())) {
            throw new IllegalArgumentException("Không thể chuyển hàng trong cùng một chi nhánh!");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Danh sách sản phẩm không được rỗng!");
        }

        Branch fromBranch = branchRepository.findById(request.getFromBranchId())
                .orElseThrow(() -> new IllegalArgumentException("Chi nhánh xuất không tồn tại!"));
        
        Branch toBranch = branchRepository.findById(request.getToBranchId())
                .orElseThrow(() -> new IllegalArgumentException("Chi nhánh nhận không tồn tại!"));

        // Sinh mã: TRN-yyyyMMdd-XXXXX
        String code = "TRN-" + System.currentTimeMillis() + "-" + (100 + new Random().nextInt(900));

        OrderTransaction tx = new OrderTransaction();
        tx.setBranchId(toBranch.getId()); // Lưu chi nhánh nhận để quản lý dữ liệu nội bộ
        tx.setFromBranchId(fromBranch.getId());
        tx.setToBranchId(toBranch.getId());
        tx.setCreatedBy(creatorId);
        tx.setCode(code);
        tx.setStatus("PENDING");
        tx.setTransactionType("TRANSFER");
        tx.setNote(request.getNote());

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<Long> selectedProductUnitIds = new ArrayList<>();

        for (TransferItemRequest itemReq : request.getItems()) {
            if (itemReq.getProductUnitId() == null) {
                throw new IllegalArgumentException("Sản phẩm và đơn vị không hợp lệ!");
            }
            if (selectedProductUnitIds.contains(itemReq.getProductUnitId())) {
                throw new IllegalArgumentException("Không được chọn trùng sản phẩm!");
            }
            selectedProductUnitIds.add(itemReq.getProductUnitId());

            if (itemReq.getQuantity() == null || itemReq.getQuantity() <= 0) {
                throw new IllegalArgumentException("Số lượng chuyển phải lớn hơn 0!");
            }
            if (itemReq.getImportPrice() == null || itemReq.getImportPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Giá nhập không được âm!");
            }

            ProductUnit productUnit = productUnitRepository.findById(itemReq.getProductUnitId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm quy đổi!"));

            // Validate tồn kho của chi nhánh xuất
            Inventory senderInventory = inventoryRepository.findByBranchIdAndProductUnitId(fromBranch.getId(), productUnit.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Chi nhánh xuất không có sản phẩm: " + productUnit.getProduct().getName()));
            
            if (senderInventory.getStock() < itemReq.getQuantity()) {
                throw new IllegalArgumentException(String.format("Chi nhánh xuất chỉ còn %d sản phẩm %s, không đủ %d để chuyển!",
                        senderInventory.getStock(), productUnit.getProduct().getName(), itemReq.getQuantity()));
            }

            OrderTransactionDetail detail = new OrderTransactionDetail();
            detail.setOrderTransaction(tx);
            detail.setProductUnit(productUnit);
            detail.setQuantity(itemReq.getQuantity());
            detail.setSalePrice(BigDecimal.ZERO);
            detail.setImportPrice(itemReq.getImportPrice());
            detail.setDiscountAmount(BigDecimal.ZERO);

            BigDecimal lineTotal = itemReq.getImportPrice().multiply(new BigDecimal(itemReq.getQuantity()));
            detail.setTotalAmount(lineTotal);

            tx.getDetails().add(detail);
            totalAmount = totalAmount.add(lineTotal);
        }

        tx.setTotalAmount(totalAmount);
        tx.setDiscountAmount(BigDecimal.ZERO);
        tx.setFinalAmount(totalAmount);
        tx.setPaidAmount(BigDecimal.ZERO);
        tx.setChangeAmount(BigDecimal.ZERO);

        orderTransactionRepository.save(tx);
    }

    @Override
    @Transactional
    public void approveTransferRequest(Long id, Long approverId) {
        OrderTransaction tx = orderTransactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu chuyển kho!"));

        if (!"PENDING".equals(tx.getStatus())) {
            throw new IllegalArgumentException("Chỉ được duyệt phiếu ở trạng thái PENDING!");
        }

        // Validate tồn kho một lần nữa để đảm bảo an toàn trước khi duyệt
        for (OrderTransactionDetail detail : tx.getDetails()) {
            Inventory senderInventory = inventoryRepository.findByBranchIdAndProductUnitId(tx.getFromBranchId(), detail.getProductUnit().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Chi nhánh xuất không có sản phẩm này!"));
            if (senderInventory.getStock() < detail.getQuantity()) {
                throw new IllegalArgumentException("Chi nhánh xuất không đủ số lượng tồn kho để duyệt phiếu này!");
            }
        }

        // Trừ tồn kho từ chi nhánh xuất
        for (OrderTransactionDetail detail : tx.getDetails()) {
            Inventory senderInventory = inventoryRepository.findByBranchIdAndProductUnitId(tx.getFromBranchId(), detail.getProductUnit().getId()).get();
            senderInventory.setStock(senderInventory.getStock() - detail.getQuantity());
            inventoryRepository.save(senderInventory);
        }

        // Cộng tồn kho cho chi nhánh nhận
        for (OrderTransactionDetail detail : tx.getDetails()) {
            Optional<Inventory> optReceiverInventory = inventoryRepository.findByBranchIdAndProductUnitId(tx.getToBranchId(), detail.getProductUnit().getId());
            if (optReceiverInventory.isPresent()) {
                Inventory receiverInventory = optReceiverInventory.get();
                receiverInventory.setStock(receiverInventory.getStock() + detail.getQuantity());
                inventoryRepository.save(receiverInventory);
            } else {
                Inventory newInventory = new Inventory();
                newInventory.setBranch(branchRepository.findById(tx.getToBranchId()).orElse(null));
                newInventory.setProductUnit(detail.getProductUnit());
                newInventory.setStock(detail.getQuantity());
                newInventory.setMinStock(0);
                inventoryRepository.save(newInventory);
            }
        }

        tx.setStatus("COMPLETED");
        orderTransactionRepository.save(tx);
    }

    @Override
    @Transactional
    public void rejectTransferRequest(Long id, Long rejectorId, String reason) {
        OrderTransaction tx = orderTransactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu chuyển kho!"));

        if (!"PENDING".equals(tx.getStatus())) {
            throw new IllegalArgumentException("Chỉ được từ chối phiếu ở trạng thái PENDING!");
        }

        tx.setStatus("CANCELLED");
        
        String finalNote = tx.getNote();
        if (reason != null && !reason.trim().isEmpty()) {
            if (finalNote == null || finalNote.trim().isEmpty()) {
                finalNote = "Lý do từ chối: " + reason.trim();
            } else {
                finalNote += " | Lý do từ chối: " + reason.trim();
            }
            tx.setNote(finalNote);
        }
        
        orderTransactionRepository.save(tx);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransferResponse> getTransfers(Long fromBranchId, Long toBranchId, String status, Long userBranchId, Pageable pageable) {
        // Chúng ta lấy tất cả phiếu chuyển và lọc thủ công, hoặc có thể dùng custom query.
        // Để đơn giản, hãy lấy tất cả giao dịch TRANSFER và lọc.
        // Hoặc chúng ta có thể thêm query trong repository:
        // @Query("SELECT o FROM OrderTransaction o WHERE o.transactionType = 'TRANSFER' AND (:fromBranchId IS NULL OR o.fromBranchId = :fromBranchId) AND (:toBranchId IS NULL OR o.toBranchId = :toBranchId) AND (:status IS NULL OR o.status = :status)")
        
        List<OrderTransaction> allTransfers = orderTransactionRepository.findAll();
        List<TransferResponse> filtered = new ArrayList<>();
        
        for (OrderTransaction tx : allTransfers) {
            if (!"TRANSFER".equals(tx.getTransactionType())) continue;
            
            // Phân quyền dữ liệu theo chi nhánh của nhân viên
            if (userBranchId != null) {
                if (!userBranchId.equals(tx.getFromBranchId()) && !userBranchId.equals(tx.getToBranchId())) {
                    continue; // Bỏ qua nếu phiếu không liên quan đến chi nhánh của nhân viên này
                }
            }

            if (fromBranchId != null && !fromBranchId.equals(tx.getFromBranchId())) continue;
            if (toBranchId != null && !toBranchId.equals(tx.getToBranchId())) continue;
            if (status != null && !status.isEmpty() && !status.equals(tx.getStatus())) continue;
            
            filtered.add(mapToResponse(tx));
        }

        // Phân trang
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());
        
        List<TransferResponse> pageContent = new ArrayList<>();
        if (start <= end) {
             pageContent = filtered.subList(start, end);
        }
        
        return new PageImpl<>(pageContent, pageable, filtered.size());
    }

    @Override
    @Transactional(readOnly = true)
    public TransferResponse getTransferById(Long id) {
        OrderTransaction tx = orderTransactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu chuyển kho!"));
        return mapToResponse(tx);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransferDetailResponse> getTransferDetails(Long transactionId) {
        OrderTransaction tx = orderTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu chuyển kho!"));
        
        List<TransferDetailResponse> details = new ArrayList<>();
        for (OrderTransactionDetail detail : tx.getDetails()) {
            TransferDetailResponse res = new TransferDetailResponse();
            res.setId(detail.getId());
            res.setProductUnitId(detail.getProductUnit().getId());
            res.setProductName(detail.getProductUnit().getProduct().getName());
            res.setSku(detail.getProductUnit().getSku());
            res.setUnitName(detail.getProductUnit().getUnit().getName());
            res.setQuantity(detail.getQuantity());
            res.setPrice(detail.getImportPrice());
            res.setLineTotal(detail.getTotalAmount());
            details.add(res);
        }
        return details;
    }

    private TransferResponse mapToResponse(OrderTransaction tx) {
        TransferResponse res = new TransferResponse();
        res.setId(tx.getId());
        res.setCode(tx.getCode());
        res.setFromBranchId(tx.getFromBranchId());
        res.setToBranchId(tx.getToBranchId());
        
        branchRepository.findById(tx.getFromBranchId()).ifPresent(b -> res.setFromBranchName(b.getName()));
        branchRepository.findById(tx.getToBranchId()).ifPresent(b -> res.setToBranchName(b.getName()));
        
        employeeRepository.findById(tx.getCreatedBy()).ifPresent(e -> res.setCreatorName(e.getFullName()));
        
        res.setTotalAmount(tx.getTotalAmount());
        res.setStatus(tx.getStatus());
        res.setCreatedAt(tx.getCreatedAt());
        res.setNote(tx.getNote());
        return res;
    }
}
