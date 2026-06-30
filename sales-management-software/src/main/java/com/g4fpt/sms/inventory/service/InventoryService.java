package com.g4fpt.sms.inventory.service;

import com.g4fpt.sms.inventory.dto.InventoryBranchSummaryResponse;
import com.g4fpt.sms.inventory.dto.InventoryDetailResponse;
import com.g4fpt.sms.inventory.dto.InventoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InventoryService {
    Page<InventoryBranchSummaryResponse> getInventorySummaryByBranch(Pageable pageable);

    // Lấy chi tiết kho của 1 chi nhánh (toàn bộ, không lọc)
    Page<InventoryDetailResponse> getInventoryDetailByBranchId(Long branchId, Pageable pageable);

    Page<InventoryDetailResponse> searchInventory(Long branchId, String keyword, Pageable pageable);

    //Lọc chỉ lấy hàng sắp hết (stock <= minStock)
    Page<InventoryDetailResponse> getLowStockInventory(Long branchId, Pageable pageable);

    // Dùng cho Sửa: Lấy dữ liệu DTO để đẩy lên Form
    InventoryRequest getInventoryRequestById(Long id);

    // Lưu mới
    void createInventory(InventoryRequest request);

    // Cập nhật
    void updateInventory(Long id, InventoryRequest request);

    // Xóa
    void deleteInventory(Long id);
}
