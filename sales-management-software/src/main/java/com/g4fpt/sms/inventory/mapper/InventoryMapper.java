package com.g4fpt.sms.inventory.mapper;

import com.g4fpt.sms.branch.entity.Branch;
import com.g4fpt.sms.inventory.dto.request.InventoryRequest;
import com.g4fpt.sms.inventory.dto.response.InventoryResponse;
import com.g4fpt.sms.inventory.entity.Inventory;
import com.g4fpt.sms.product.entity.ProductUnit;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public Inventory toEntity(
            InventoryRequest request,
            Branch branch,
            ProductUnit productUnit
    ) {
        return Inventory.builder()
                .branch(branch)
                .productUnit(productUnit)
                .stock(request.getStock())
                .minStock(request.getMinStock())
                .maxStock(request.getMaxStock())
                .positionInShop(request.getPositionInShop())
                .build();
    }

    public InventoryResponse toResponse(Inventory inventory) {

        String stockStatus = "OK";

        if (inventory.getStock() <= inventory.getMinStock()) {
            stockStatus = "LOW_STOCK";
        }

        return InventoryResponse.builder()
                .id(inventory.getId())

                .branchId(inventory.getBranch().getId())
                .branchName(inventory.getBranch().getName())

                .productUnitId(inventory.getProductUnit().getId())
                .productName(
                        inventory.getProductUnit()
                                .getProduct()
                                .getName()
                )
                .sku(inventory.getProductUnit().getSku())

                .stock(inventory.getStock())
                .minStock(inventory.getMinStock())
                .maxStock(inventory.getMaxStock())
                .positionInShop(inventory.getPositionInShop())
                .stockStatus(stockStatus)
                .build();
    }

    public void updateEntity(
            Inventory inventory,
            InventoryRequest request
    ) {
        inventory.setStock(request.getStock());
        inventory.setMinStock(request.getMinStock());
        inventory.setMaxStock(request.getMaxStock());
        inventory.setPositionInShop(request.getPositionInShop());
    }
}