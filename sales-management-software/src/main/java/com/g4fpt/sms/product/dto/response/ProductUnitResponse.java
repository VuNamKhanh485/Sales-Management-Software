package com.g4fpt.sms.product.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductUnitResponse {
    private Long id;
    private UnitResponse unit;
    private int conventionValue;
    private BigDecimal price;
    private String barcodeUnit;
    private Boolean isBaseUnit;
    private String sku;
}
