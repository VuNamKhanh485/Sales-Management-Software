package com.g4fpt.sms.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDetailResponse {
    private Long inventoryId;
    private String productName;
    private String sku;
    private String unitName;
    private Integer stock;
    private Integer minStock;
    private BigDecimal price;
    private BigDecimal totalValue;
}

