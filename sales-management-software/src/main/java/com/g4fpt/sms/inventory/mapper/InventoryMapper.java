package com.g4fpt.sms.inventory.mapper;

import com.g4fpt.sms.branch.entity.Branch;
import com.g4fpt.sms.inventory.dto.InventoryBranchSummaryResponse;
import com.g4fpt.sms.inventory.dto.InventoryDetailResponse;
import com.g4fpt.sms.inventory.dto.InventoryRequest;
import com.g4fpt.sms.inventory.entity.Inventory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
public class InventoryMapper {

    public InventoryBranchSummaryResponse toSummaryResponse(Branch branch, Long totalProductCount) {
        if (branch == null) {
            return null;
        }

        InventoryBranchSummaryResponse response = new InventoryBranchSummaryResponse();
        response.setBranchId(branch.getId());
        response.setBranchCode(branch.getBranchCode());
        response.setBranchName(branch.getName());
        response.setStatus(branch.getStatus());
        response.setTotalProductCount(totalProductCount != null ? totalProductCount : 0L);

        return response;
    }

    // Chuyển đổi từ Inventory (Entity) sang InventoryDetailResponse (DTO)
    public InventoryDetailResponse toDetailResponse(Inventory inventory) {
        if (inventory == null) {
            return null;
        }

        InventoryDetailResponse response = new InventoryDetailResponse();

        // Lấy ID của bản ghi Inventory (dùng cho nút Sửa, Xóa)
        response.setInventoryId(inventory.getId());

        // Lấy tên sản phẩm từ bảng Product
        response.setProductName(inventory.getProductUnit().getProduct().getName());

        // Lấy SKU từ ProductUnit (dùng để hiển thị và tìm kiếm)
        response.setSku(inventory.getProductUnit().getSku());

        // Lấy tên đơn vị từ bảng Unit
        response.setUnitName(inventory.getProductUnit().getUnit().getName());

        // Lấy số lượng tồn kho
        response.setStock(inventory.getStock());
        response.setMinStock(inventory.getMinStock());

        // Lấy giá bán
        BigDecimal price = inventory.getProductUnit().getPrice() != null ? inventory.getProductUnit().getPrice() : BigDecimal.ZERO;
        response.setPrice(price);

        // Tính thành tiền = tồn kho * giá bán
        int stock = inventory.getStock() != null ? inventory.getStock() : 0;
        response.setTotalValue(price.multiply(new BigDecimal(stock)));

        return response;
    }


    // Dùng khi Thêm mới: copy từ DTO sang Entity rỗng
    public Inventory toEntity(InventoryRequest request) {
        if (request == null) {
            return null;
        }
        Inventory entity = new com.g4fpt.sms.inventory.entity.Inventory();
        entity.setStock(request.getStock());
        entity.setMinStock(request.getMinStock());
        entity.setMaxStock(request.getMaxStock());
        entity.setPositionInShop(request.getPositionInShop());
        // branch và productUnit sẽ được set ở tầng Service
        return entity;
    }

    //  copy từ DTO đè vào Entity đã có sẵn
    public void updateEntity(InventoryRequest request, Inventory entity) {
        if (request == null || entity == null) {
            return;
        }
        entity.setStock(request.getStock());
        entity.setMinStock(request.getMinStock());
        entity.setMaxStock(request.getMaxStock());
        entity.setPositionInShop(request.getPositionInShop());
    }
}
