package com.g4fpt.sms.payment.service;

import com.g4fpt.sms.payment.dto.request.PaymentMethodCreateRequest;
import com.g4fpt.sms.payment.dto.request.PaymentMethodUpdateRequest;
import com.g4fpt.sms.payment.dto.response.PaymentMethodResponse;
import com.g4fpt.sms.payment.entity.PaymentMethod;

import java.util.List;

public interface PaymentMethodService {
    List<PaymentMethodResponse> search(String keyword, String status);
    PaymentMethodResponse getById(Long id);
    PaymentMethod getEntityById(Long id);
    void create(PaymentMethodCreateRequest request);
    void update(Long id, PaymentMethodUpdateRequest request);
    void delete(Long id);
}
