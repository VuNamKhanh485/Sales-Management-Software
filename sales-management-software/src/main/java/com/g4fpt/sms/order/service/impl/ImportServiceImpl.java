package com.g4fpt.sms.order.service.impl;

import com.g4fpt.sms.branch.entity.Branch;
import com.g4fpt.sms.branch.repository.BranchRepository;
import com.g4fpt.sms.employee.entity.Employee;
import com.g4fpt.sms.employee.repository.EmployeeRepository;
import com.g4fpt.sms.inventory.entity.Inventory;
import com.g4fpt.sms.inventory.repository.InventoryRepository;
import com.g4fpt.sms.order.dto.ImportDetailResponse;
import com.g4fpt.sms.order.dto.ImportItemRequest;
import com.g4fpt.sms.order.dto.ImportRequest;
import com.g4fpt.sms.order.dto.ImportResponse;
import com.g4fpt.sms.order.entity.OrderTransaction;
import com.g4fpt.sms.order.entity.OrderTransactionDetail;
import com.g4fpt.sms.order.mapper.ImportMapper;
import com.g4fpt.sms.order.repository.OrderTransactionRepository;
import com.g4fpt.sms.order.service.ImportService;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.repository.ProductUnitRepository;
import com.g4fpt.sms.supplier.entity.Supplier;
import com.g4fpt.sms.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ImportServiceImpl implements ImportService {

    private final OrderTransactionRepository orderTransactionRepository;
    private final BranchRepository branchRepository;
    private final SupplierRepository supplierRepository;
    private final EmployeeRepository employeeRepository;
    private final ProductUnitRepository productUnitRepository;
    private final InventoryRepository inventoryRepository;
    private final ImportMapper importMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ImportResponse> getAllImports(String status, String keyword) {
        List<OrderTransaction> txList = orderTransactionRepository.searchImports(status, keyword);
        List<ImportResponse> responseList = new ArrayList<>();

        for (OrderTransaction tx : txList) {
            // Lấy tên chi nhánh
            Branch branch = branchRepository.findById(tx.getBranchId()).orElse(null);
            String branchName = (branch != null) ? branch.getName() : "Không xác định";

            // Lấy tên nhà cung cấp
            String supplierName = (tx.getSupplier() != null) ? tx.getSupplier().getName() : "Không xác định";

            // Lấy tên người tạo
            Employee creator = employeeRepository.findById(tx.getCreatedBy()).orElse(null);
            String creatorName = (creator != null) ? creator.getFullName() : "Không xác định";

            responseList.add(importMapper.toResponse(tx, branchName, supplierName, creatorName));
        }

        return responseList;
    }

    @Override
    @Transactional(readOnly = true)
    public ImportResponse getImportById(Long id) {
        OrderTransaction tx = orderTransactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu nhập với ID: " + id));

        Branch branch = branchRepository.findById(tx.getBranchId()).orElse(null);
        String branchName = (branch != null) ? branch.getName() : "Không xác định";

        String supplierName = (tx.getSupplier() != null) ? tx.getSupplier().getName() : "Không xác định";

        Employee creator = employeeRepository.findById(tx.getCreatedBy()).orElse(null);
        String creatorName = (creator != null) ? creator.getFullName() : "Không xác định";

        return importMapper.toResponse(tx, branchName, supplierName, creatorName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImportDetailResponse> getImportDetails(Long id) {
        OrderTransaction tx = orderTransactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu nhập với ID: " + id));

        List<ImportDetailResponse> details = new ArrayList<>();
        for (OrderTransactionDetail detail : tx.getDetails()) {
            details.add(importMapper.toDetailResponse(detail));
        }

        return details;
    }

    @Override
    @Transactional
    public void createImportRequest(ImportRequest request, Long employeeId) {
        // Validation
        if (request.getBranchId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn chi nhánh cần nhập hàng!");
        }
        if (request.getSupplierId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn nhà cung cấp!");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Danh sách sản phẩm không được rỗng!");
        }

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new IllegalArgumentException("Chi nhánh không tồn tại!"));

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new IllegalArgumentException("Nhà cung cấp không tồn tại!"));

        // Generate unique code: IMP-yyyyMMdd-XXXXX
        String code = "IMP-" + System.currentTimeMillis() + "-" + (100 + new Random().nextInt(900));

        OrderTransaction tx = new OrderTransaction();
        tx.setBranchId(branch.getId());
        tx.setSupplier(supplier);
        tx.setCreatedBy(employeeId);
        tx.setCode(code);
        tx.setStatus("PENDING");
        tx.setTransactionType("IMPORT");
        tx.setNote(request.getNote());

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<Long> selectedProductUnitIds = new ArrayList<>();

        for (ImportItemRequest itemReq : request.getItems()) {
            if (itemReq.getProductUnitId() == null) {
                throw new IllegalArgumentException("Sản phẩm và đơn vị không hợp lệ!");
            }
            if (selectedProductUnitIds.contains(itemReq.getProductUnitId())) {
                throw new IllegalArgumentException("Không được chọn trùng sản phẩm và đơn vị trong cùng một phiếu nhập!");
            }
            selectedProductUnitIds.add(itemReq.getProductUnitId());

            if (itemReq.getQuantity() == null || itemReq.getQuantity() <= 0) {
                throw new IllegalArgumentException("Số lượng nhập phải lớn hơn 0!");
            }
            if (itemReq.getImportPrice() == null || itemReq.getImportPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Giá nhập không được âm!");
            }

            ProductUnit productUnit = productUnitRepository.findById(itemReq.getProductUnitId())
                    .orElseThrow(
                            () -> new IllegalArgumentException("Không tìm thấy sản phẩm với đơn vị quy đổi đã chọn!"));

            OrderTransactionDetail detail = new OrderTransactionDetail();
            detail.setOrderTransaction(tx);
            detail.setProductUnit(productUnit);
            detail.setQuantity(itemReq.getQuantity());
            detail.setSalePrice(BigDecimal.ZERO); // Mặc định giá bán bằng 0 khi nhập hàng
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
    public void approveImportRequest(Long id) {
        OrderTransaction tx = orderTransactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu nhập với ID: " + id));

        if (!"PENDING".equals(tx.getStatus())) {
            throw new IllegalArgumentException("Chỉ cho phép duyệt phiếu ở trạng thái PENDING!");
        }

        // Cập nhật trạng thái phiếu nhập
        tx.setStatus("COMPLETED");
        orderTransactionRepository.save(tx);

        // Duyệt từng chi tiết để cộng dồn tồn kho
        for (OrderTransactionDetail detail : tx.getDetails()) {
            Long branchId = tx.getBranchId();
            Long productUnitId = detail.getProductUnit().getId();
            Integer quantity = detail.getQuantity();

            Optional<Inventory> optInventory = inventoryRepository.findByBranchIdAndProductUnitId(branchId,
                    productUnitId);

            if (optInventory.isPresent()) {
                // Đã tồn tại trong kho -> cộng thêm số lượng nhập
                Inventory inventory = optInventory.get();
                inventory.setStock(inventory.getStock() + quantity);
                inventoryRepository.save(inventory);
            } else {
                // Chưa tồn tại trong kho -> tạo mới bản ghi Inventory
                Inventory inventory = new Inventory();
                inventory.setBranch(branchRepository.findById(branchId).orElse(null));
                inventory.setProductUnit(detail.getProductUnit());
                inventory.setStock(quantity);
                inventory.setMinStock(0); // Mặc định tồn tối thiểu là 0
                inventoryRepository.save(inventory);
            }
        }
    }

    @Override
    @Transactional
    public void rejectImportRequest(Long id, String reason) {
        OrderTransaction tx = orderTransactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu nhập với ID: " + id));

        if (!"PENDING".equals(tx.getStatus())) {
            throw new IllegalArgumentException("Chỉ cho phép từ chối phiếu ở trạng thái PENDING!");
        }

        // Đổi trạng thái sang CANCELLED
        tx.setStatus("CANCELLED");

        // Ghi lại lý do từ chối vào note
        String finalNote = tx.getNote();
        if (reason != null && !reason.trim().isEmpty()) {
            if (finalNote == null || finalNote.trim().isEmpty()) {
                finalNote = "Lý do từ chối: " + reason.trim();
            } else {
                finalNote = finalNote + " (Lý do từ chối: " + reason.trim() + ")";
            }
        }
        tx.setNote(finalNote);

        orderTransactionRepository.save(tx);
        // KHÔNG cập nhật inventory
    }
}
