package com.g4fpt.sms.order.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class POSCartItemRequest {
    private Long productUnitId;
    private Integer quantity;
    private BigDecimal itemDiscount;
}