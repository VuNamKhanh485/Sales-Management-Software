package com.g4fpt.sms.product.dto.response;

import com.g4fpt.sms.product.entity.Brand;
import com.g4fpt.sms.product.enums.ProductStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductUnitResponse {
    private Long id;
    private Long productId;
    private Long unitId;
    private int conventionValue;
    private BigDecimal unitPrice;
    private String barcodeUnit;
    private Boolean isBaseUnit;
    private String sku;
}
