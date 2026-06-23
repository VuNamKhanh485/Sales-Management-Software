package com.g4fpt.sms.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class POSCartItemRequest {
    private Long productUnitId;
    private int quantity;
}