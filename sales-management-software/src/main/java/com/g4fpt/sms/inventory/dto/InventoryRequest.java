package com.g4fpt.sms.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest {
    private Long id;
    private Long branchId;
    private Long productId;
    private Long unitId;
    private Long productUnitId;
    private Integer stock;
    private Integer minStock;
    private Integer maxStock;
    private String positionInShop;
}
