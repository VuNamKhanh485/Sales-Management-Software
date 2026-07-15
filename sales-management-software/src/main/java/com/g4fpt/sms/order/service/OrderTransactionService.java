package com.g4fpt.sms.order.service;

import com.g4fpt.sms.order.dto.POSCheckoutRequest;
import com.g4fpt.sms.order.entity.OrderTransaction;
import com.g4fpt.sms.voucher.entity.Voucher;

import java.math.BigDecimal;

public interface OrderTransactionService {
    /** Xử lý checkout: validate, tính tiền, trừ kho, lưu đơn hàng. */
    OrderTransaction processCheckout(POSCheckoutRequest request);
    /** Kiểm tra mã giảm giá còn hiệu lực. Ném RuntimeException nếu lỗi. */
    Voucher validateVoucher(String code, BigDecimal totalAmount, Long customerId);
    /** Kiểm tra + tính số tiền giảm (dùng cho apply-voucher ở Controller). */
    BigDecimal calculateVoucherDiscount(String code, BigDecimal totalAmount, Long customerId);
}