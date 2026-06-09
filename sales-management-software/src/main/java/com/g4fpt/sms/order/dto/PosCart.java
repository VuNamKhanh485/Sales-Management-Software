package com.g4fpt.sms.order.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class PosCart {
    private List<PosCartItem> items = new ArrayList<>();
    private Long customerId;
    private String voucherCode;
    private Long paymentMethodId = 2L;
    private BigDecimal givenAmount = BigDecimal.ZERO;

    public BigDecimal getTotalAmount() {
        return items.stream()
                .map(PosCartItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}