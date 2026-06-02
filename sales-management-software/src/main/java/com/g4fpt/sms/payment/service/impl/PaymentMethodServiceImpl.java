package com.g4fpt.sms.payment.service.impl;

import com.g4fpt.sms.common.exception.AppException;
import com.g4fpt.sms.common.exception.ErrorCode;
import com.g4fpt.sms.payment.dto.request.PaymentMethodCreateRequest;
import com.g4fpt.sms.payment.dto.request.PaymentMethodUpdateRequest;
import com.g4fpt.sms.payment.dto.response.PaymentMethodResponse;
import com.g4fpt.sms.payment.entity.PaymentMethod;
import com.g4fpt.sms.payment.enums.PaymentStatus;
import com.g4fpt.sms.payment.mapper.PaymentMethodMapper;
import com.g4fpt.sms.payment.repository.PaymentMethodRepository;
import com.g4fpt.sms.payment.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentMethodMapper paymentMethodMapper;

    @Override
    public List<PaymentMethodResponse> search(String keyword, String status) {
        String kw = (keyword != null && keyword.isBlank()) ? null : keyword;
        PaymentStatus ps = null;
        if (status != null && !status.isBlank()) {
            ps = PaymentStatus.valueOf(status);
        }
        return paymentMethodRepository.search(kw, ps)
                .stream()
                .map(paymentMethodMapper::toResponse)
                .toList();
    }

    @Override
    public PaymentMethodResponse getById(Long id) {
        return paymentMethodMapper.toResponse(getEntityById(id));
    }

    @Override
    public PaymentMethod getEntityById(Long id) {
        return paymentMethodRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    @Override
    @Transactional
    public void create(PaymentMethodCreateRequest request) {
        if (paymentMethodRepository.existsByCode(request.getCode().toUpperCase().trim())) {
            throw new AppException(ErrorCode.PAYMENT_CODE_EXISTED);
        }
        paymentMethodRepository.save(paymentMethodMapper.toEntity(request));
    }

    @Override
    @Transactional
    public void update(Long id, PaymentMethodUpdateRequest request) {
        if (paymentMethodRepository.existsByCodeAndIdNot(
                request.getCode().toUpperCase().trim(), id)) {
            throw new AppException(ErrorCode.PAYMENT_CODE_EXISTED);
        }
        PaymentMethod entity = getEntityById(id);
        paymentMethodMapper.updateEntity(entity, request);
        paymentMethodRepository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        paymentMethodRepository.delete(getEntityById(id));
    }
}
