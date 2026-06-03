package com.g4fpt.sms.voucher.service.impl;

import com.g4fpt.sms.common.exception.AppException;
import com.g4fpt.sms.common.exception.ErrorCode;
import com.g4fpt.sms.voucher.dto.request.VoucherCreateRequest;
import com.g4fpt.sms.voucher.dto.request.VoucherUpdateRequest;
import com.g4fpt.sms.voucher.dto.response.VoucherResponse;
import com.g4fpt.sms.voucher.entity.Voucher;
import com.g4fpt.sms.voucher.enums.VoucherStatus;
import com.g4fpt.sms.voucher.mapper.VoucherMapper;
import com.g4fpt.sms.voucher.repository.VoucherRepository;
import com.g4fpt.sms.voucher.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;

    @Override
    public List<VoucherResponse> search(String keyword, String status) {
        String kw = (keyword != null && keyword.isBlank()) ? null : keyword;
        VoucherStatus vs = null;
        if (status != null && !status.isBlank()) {
            vs = VoucherStatus.valueOf(status);
        }
        return voucherRepository.search(kw, vs)
                .stream()
                .map(voucherMapper::toResponse)
                .toList();
    }

    @Override
    public VoucherResponse getById(Long id) {
        return voucherMapper.toResponse(getEntityById(id));
    }

    @Override
    public Voucher getEntityById(Long id) {
        return voucherRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
    }

    @Override
    @Transactional
    public void create(VoucherCreateRequest request) {
        if (voucherRepository.existsByCode(request.getCode().toUpperCase().trim())) {
            throw new AppException(ErrorCode.VOUCHER_CODE_EXISTED);
        }
        voucherRepository.save(voucherMapper.toEntity(request));
    }

    @Override
    @Transactional
    public void update(Long id, VoucherUpdateRequest request) {
        if (voucherRepository.existsByCodeAndIdNot(
                request.getCode().toUpperCase().trim(), id)) {
            throw new AppException(ErrorCode.VOUCHER_CODE_EXISTED);
        }
        Voucher voucher = getEntityById(id);
        voucherMapper.updateEntity(voucher, request);
        voucherRepository.save(voucher);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        voucherRepository.delete(getEntityById(id));
    }
}