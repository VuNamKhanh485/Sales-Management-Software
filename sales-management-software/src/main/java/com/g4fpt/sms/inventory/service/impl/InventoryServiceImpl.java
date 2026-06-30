package com.g4fpt.sms.inventory.service.impl;

import com.g4fpt.sms.auth.security.CustomUserDetails;
import com.g4fpt.sms.branch.entity.Branch;
import com.g4fpt.sms.branch.repository.BranchRepository;
import com.g4fpt.sms.inventory.dto.InventoryBranchSummaryResponse;
import com.g4fpt.sms.inventory.dto.InventoryDetailResponse;
import com.g4fpt.sms.inventory.dto.InventoryRequest;
import com.g4fpt.sms.inventory.entity.Inventory;
import com.g4fpt.sms.inventory.mapper.InventoryMapper;
import com.g4fpt.sms.inventory.repository.InventoryRepository;
import com.g4fpt.sms.inventory.service.InventoryService;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.repository.ProductUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final BranchRepository branchRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final ProductUnitRepository productUnitRepository;

    @Override
    public Page<InventoryBranchSummaryResponse> getInventorySummaryByBranch(Pageable pageable) {

        // 1. Lấy ra các chi nhánh có trong Database theo phân trang
        Page<Branch> branches = branchRepository.findAll(pageable);

        // 2. Map từng chi nhánh sang InventoryBranchSummaryResponse
        return branches.map(branch -> {
            // Đếm xem có bao nhiêu SẢN PHẨM khác nhau trong kho của chi nhánh này
            long count = inventoryRepository.countDistinctProductByBranchId(branch.getId());
            // Gộp thông tin chi nhánh và số lượng mặt hàng vào DTO (thông qua Mapper)
            return inventoryMapper.toSummaryResponse(branch, count);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryDetailResponse> getInventoryDetailByBranchId(Long branchId, Pageable pageable) {
        Page<Inventory> inventories = inventoryRepository.findByBranchId(branchId, pageable);
        return inventories.map(inventoryMapper::toDetailResponse);
    }


    // Tìm kiếm sản phẩm trong kho theo tên hoặc SKU
    @Override
    @Transactional(readOnly = true)
    public Page<InventoryDetailResponse> searchInventory(Long branchId, String keyword, Pageable pageable) {

        // Nếu keyword rỗng, gọi lại hàm lấy toàn bộ (tái sử dụng code có sẵn)
        if (keyword == null || keyword.trim().isEmpty()) {
            return getInventoryDetailByBranchId(branchId, pageable);
        }

        // Gọi Repository để tìm theo tên sản phẩm hoặc SKU
        Page<Inventory> inventories = inventoryRepository.searchByBranchIdAndKeyword(
                branchId, keyword.trim(), pageable);

        return inventories.map(inventoryMapper::toDetailResponse);
    }


    // Lọc hàng sắp hết (stock <= minStock)
    @Override
    @Transactional(readOnly = true)
    public Page<InventoryDetailResponse> getLowStockInventory(Long branchId, Pageable pageable) {
        Page<Inventory> inventories = inventoryRepository.findLowStockByBranchId(branchId, pageable);
        return inventories.map(inventoryMapper::toDetailResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryRequest getInventoryRequestById(Long id) {
        // Tìm bản ghi Inventory theo ID
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tồn kho với ID: " + id));

        // Tạo DTO rỗng và copy dữ liệu sang
        InventoryRequest request = new InventoryRequest();

        request.setId(inventory.getId());
        request.setBranchId(inventory.getBranch().getId());
        request.setProductUnitId(inventory.getProductUnit().getId());

        request.setProductId(inventory.getProductUnit().getProduct().getId());
        request.setUnitId(inventory.getProductUnit().getUnit().getId());
        request.setStock(inventory.getStock());
        request.setMinStock(inventory.getMinStock());
        request.setMaxStock(inventory.getMaxStock());
        request.setPositionInShop(inventory.getPositionInShop());

        return request;
    }

    @Override
    @Transactional
    public void createInventory(InventoryRequest request) {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                if (!userDetails.hasRole("OWNER")) {
                    request.setBranchId(userDetails.getBranchId());
                }
            }
        }

        // 1. Validate: kiểm tra stock và minStock không âm
        if (request.getStock() == null || request.getStock() < 0) {
            throw new IllegalArgumentException("Số lượng tồn kho không được âm");
        }
        if (request.getMinStock() == null || request.getMinStock() < 0) {

            throw new IllegalArgumentException("Tồn tối thiểu không được âm");
        }
        if (request.getMaxStock() != null && request.getMaxStock() < request.getMinStock()) {
            throw new IllegalArgumentException("Tồn tối đa phải lớn hơn hoặc bằng tồn tối thiểu");
        }

        // 3. Tìm Branch trong Database
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chi nhánh"));

        // 4. Tìm ProductUnit theo productId + unitId mà người dùng đã chọn riêng biệt
        ProductUnit productUnit = productUnitRepository.findByProduct_IdAndUnit_Id(
                        request.getProductId(), request.getUnitId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với đơn vị đã chọn. Vui lòng kiểm tra lại."));

        // 5. Kiểm tra xem cặp (branchId, productUnitId) đã tồn tại chưa
        boolean exists = inventoryRepository.existsByBranchIdAndProductUnitId(
                request.getBranchId(), productUnit.getId());
        if (exists) {
            throw new IllegalArgumentException("Sản phẩm này đã tồn tại trong kho chi nhánh");
        }

        // 4. Tạo Entity mới từ DTO qua Mapper
        Inventory newInventory = inventoryMapper.toEntity(request);

        // 5. Gắn Branch và ProductUnit vào Entity (Mapper không làm việc này)
        newInventory.setBranch(branch);
        newInventory.setProductUnit(productUnit);

        // 6. Lưu vào Database
        inventoryRepository.save(newInventory);
    }

    @Override
    @Transactional
    public void updateInventory(Long id, InventoryRequest request) {
        // 1. Validate
        if (request.getStock() == null || request.getStock() < 0) {
            throw new IllegalArgumentException("Số lượng tồn kho không được âm");
        }
        if (request.getMinStock() == null || request.getMinStock() < 0) {
            throw new IllegalArgumentException("Tồn tối thiểu không được âm");
        }
        if (request.getMaxStock() != null && request.getMaxStock() < request.getMinStock()) {
            throw new IllegalArgumentException("Tồn tối đa phải lớn hơn hoặc bằng tồn tối thiểu");
        }

        // 2. Tìm bản ghi cũ cần sửa
        Inventory existingInventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tồn kho với ID: " + id));

        // 3. Dùng Mapper để cập nhật dữ liệu mới vào Entity cũ
        //    (Mapper chỉ cập nhật stock, minStock, maxStock, positionInShop)
        inventoryMapper.updateEntity(request, existingInventory);

        // 4. Lưu lại vào Database
        inventoryRepository.save(existingInventory);
    }

    @Override
    @Transactional
    public void deleteInventory(Long id) {
        // Tìm bản ghi cần xóa (để lấy branchId trả về nếu cần)
        if (!inventoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy tồn kho với ID: " + id);
        }
        // Xóa bản ghi khỏi Database
        inventoryRepository.deleteById(id);
    }
}
