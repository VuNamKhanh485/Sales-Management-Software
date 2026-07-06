package com.g4fpt.sms.order.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class POSCheckoutRequest {
    private Long branchId;
    private Long employeeId;
    private Long customerId;
    private String voucherCode;
    private Long paymentMethodId;
    private BigDecimal paidAmount;
    private BigDecimal vatRate;
    private String note;
    private boolean usePoints;
    private List<POSCartItemRequest> items;
}