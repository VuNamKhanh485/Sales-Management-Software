package com.g4fpt.sms.order.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PosCart {

    private List<PosCartItem> items = new ArrayList<>();

    private Long customerId;
    private String customerName;
    private String customerPhone;

    private String voucherCode;
    private BigDecimal voucherDiscount = BigDecimal.ZERO;

    private BigDecimal vatRate = new BigDecimal("0.02"); // 2%
    private Long paymentMethodId;
    private BigDecimal givenAmount = BigDecimal.ZERO;

    // Tổng tiền hàng (chưa VAT, chưa giảm giá)
    public BigDecimal getTotalAmount() {
        return items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Tiền VAT
    public BigDecimal getVatAmount() {
        return getTotalAmount()
                .multiply(vatRate)
                .setScale(0, RoundingMode.HALF_UP);
    }

    // Khách phải trả
    public BigDecimal getFinalAmount() {
        return getTotalAmount()
                .add(getVatAmount())
                .subtract(voucherDiscount)
                .max(BigDecimal.ZERO);
    }

    // Tiền thừa
    public BigDecimal getChangeAmount() {
        return givenAmount
                .subtract(getFinalAmount())
                .max(BigDecimal.ZERO);
    }
}