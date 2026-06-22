package com.g4fpt.sms.order.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PosCartItem {
    private Long productUnitId;
    private String sku;
    private String name;
    private String unitName;
    private BigDecimal price;
    private int quantity;
    private String imageUrl;

    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}