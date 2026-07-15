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
public class ImportDetailResponse {
    private String productName;
    private String unitName;
    private String sku;
    private Integer quantity;
    private BigDecimal importPrice;
    private BigDecimal totalAmount;
}
