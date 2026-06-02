package com.g4fpt.sms.payment.mapper;

import com.g4fpt.sms.payment.dto.request.PaymentMethodCreateRequest;
import com.g4fpt.sms.payment.dto.request.PaymentMethodUpdateRequest;
import com.g4fpt.sms.payment.dto.response.PaymentMethodResponse;
import com.g4fpt.sms.payment.entity.PaymentMethod;
import com.g4fpt.sms.payment.enums.PaymentStatus;
import org.springframework.stereotype.Component;

@Component
public class PaymentMethodMapper {

    public PaymentMethod toEntity(PaymentMethodCreateRequest req) {
        return PaymentMethod.builder()
                .code(req.getCode().toUpperCase().trim())
                .name(req.getName().trim())
                .status(parseStatus(req.getStatus()))
                .build();
    }

    public void updateEntity(PaymentMethod entity, PaymentMethodUpdateRequest req) {
        entity.setCode(req.getCode().toUpperCase().trim());
        entity.setName(req.getName().trim());
        entity.setStatus(parseStatus(req.getStatus()));
    }

    public PaymentMethodResponse toResponse(PaymentMethod entity) {
        return PaymentMethodResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private PaymentStatus parseStatus(String status) {
        if (status == null || status.isBlank()) return PaymentStatus.ACTIVE;
        try {
            return PaymentStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return PaymentStatus.ACTIVE;
        }
    }
}
