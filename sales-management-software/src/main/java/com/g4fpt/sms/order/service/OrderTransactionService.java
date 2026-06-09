package com.g4fpt.sms.order.service;

import com.g4fpt.sms.order.dto.POSCheckoutRequest;
import com.g4fpt.sms.order.entity.OrderTransaction;

public interface OrderTransactionService {
    OrderTransaction processCheckout(POSCheckoutRequest request);
}