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
public class TransferDetailResponse {
    private Long id;
    private Long productUnitId;
    private String productName;
    private String sku;
    private String unitName;
    private Integer quantity;
    private BigDecimal price; // giá nhập/chuyển
    private BigDecimal lineTotal;
}
