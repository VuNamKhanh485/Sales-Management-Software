package com.g4fpt.sms.order.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PosCartItem {
    private Long productUnitId;
    private String sku;
    private String name;
    private String unitName;
    private Integer quantity;
    private BigDecimal price;

    public BigDecimal getSubTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}