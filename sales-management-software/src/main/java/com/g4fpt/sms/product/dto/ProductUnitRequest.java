package com.g4fpt.sms.product.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class ProductUnitRequest {
    private Long productId;
    private Long unitId;
    private int conventionValue;
    private BigDecimal unitPrice;
    private String barcodeUnit;
    private Boolean isBaseUnit;
    private String sku;
}
