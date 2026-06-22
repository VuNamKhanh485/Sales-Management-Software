package com.g4fpt.sms.order.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class POSCheckoutRequest {
    private Long branchId;
    private Long employeeId;
    private Long customerId;
    private String voucherCode;
    private Long paymentMethodId;
    private BigDecimal paidAmount;
    private String note;
    private List<POSCartItemRequest> items;
}