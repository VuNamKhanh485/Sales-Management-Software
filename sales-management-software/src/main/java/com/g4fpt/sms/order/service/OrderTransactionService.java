package com.g4fpt.sms.order.service;

import com.g4fpt.sms.order.dto.POSCheckoutRequest;
import com.g4fpt.sms.order.entity.OrderTransaction;
import com.g4fpt.sms.voucher.entity.Voucher;

import java.math.BigDecimal;

public interface OrderTransactionService {
    OrderTransaction processCheckout(POSCheckoutRequest request);
    Voucher validateVoucher(String code, BigDecimal totalAmount, Long customerId);
}