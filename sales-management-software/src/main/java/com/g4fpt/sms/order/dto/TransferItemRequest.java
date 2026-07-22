package com.g4fpt.sms.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferItemRequest {
    private Long productUnitId;
    private Integer quantity;
    private BigDecimal importPrice;
    
    // UI Display Fields
    private String productName;
    private String sku;
    private String unitName;
    private BigDecimal lineTotal;
}
