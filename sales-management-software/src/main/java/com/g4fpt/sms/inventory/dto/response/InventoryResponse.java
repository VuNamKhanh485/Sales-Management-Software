package com.g4fpt.sms.inventory.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponse {

    private Long id;

    private Long branchId;
    private String branchName;

    private Long productUnitId;
    private String productName;
    private String sku;

    private Integer stock;
    private Integer minStock;
    private Integer maxStock;

    private String positionInShop;

    private String stockStatus;
}