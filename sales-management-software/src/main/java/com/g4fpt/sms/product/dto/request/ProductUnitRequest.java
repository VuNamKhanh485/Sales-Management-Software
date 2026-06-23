package com.g4fpt.sms.product.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class ProductUnitRequest {
    private Long id;
    @NotNull(message = "Unit is required")
    private Long unitId;
    @Min(1)
    private int conventionValue;
    @NotNull(message = "Please enter price")
    private BigDecimal price;
    @NotBlank(message = "Barcode is required")
    private String barcodeUnit;
    private Boolean isBaseUnit;
    @NotBlank(message = "SKU is required")
    private String sku;
}
