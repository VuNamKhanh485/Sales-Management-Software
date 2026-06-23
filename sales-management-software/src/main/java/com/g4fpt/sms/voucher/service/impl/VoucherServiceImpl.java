package com.g4fpt.sms.voucher.service.impl;

import com.g4fpt.sms.common.exception.AppException;
import com.g4fpt.sms.common.exception.ErrorCode;
import com.g4fpt.sms.voucher.dto.request.VoucherCreateRequest;
import com.g4fpt.sms.voucher.dto.request.VoucherUpdateRequest;
import com.g4fpt.sms.voucher.dto.response.VoucherResponse;
import com.g4fpt.sms.voucher.entity.Voucher;
import com.g4fpt.sms.voucher.enums.DiscountType;
import com.g4fpt.sms.voucher.enums.VoucherStatus;
import com.g4fpt.sms.voucher.mapper.VoucherMapper;
import com.g4fpt.sms.voucher.repository.VoucherRepository;
import com.g4fpt.sms.voucher.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;

    @Override
    @Transactional
    public VoucherResponse create(VoucherCreateRequest request) {
        if (voucherRepository.existsByCode(request.getCode().trim().toUpperCase())) {
            throw new AppException(ErrorCode.VOUCHER_CODE_EXISTED);
        }

        validateTimeRange(request.getStartAt(), request.getEndAt());
        validateDiscountValue(request.getDiscountType(), request.getDiscountValue());

        if (request.getStartAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new AppException(ErrorCode.VOUCHER_START_DATE_PAST);
        }

        Voucher voucher = voucherMapper.toEntity(request);
        return voucherMapper.toResponse(voucherRepository.save(voucher));
    }

    @Override
    @Transactional
    public VoucherResponse update(Long id, VoucherUpdateRequest request) {
        Voucher voucher = findById(id);

        validateTimeRange(request.getStartAt(), request.getEndAt());
        validateDiscountValue(request.getDiscountType(), request.getDiscountValue());

        if (request.getCode() != null && !request.getCode().trim().toUpperCase().equals(voucher.getCode())) {
            if (voucherRepository.existsByCodeAndIdNot(request.getCode().trim().toUpperCase(), id)) {
                throw new AppException(ErrorCode.VOUCHER_CODE_EXISTED);
            }
            voucher.setCode(request.getCode().trim().toUpperCase());
        }

        voucher.setName(request.getName().trim());
        voucher.setDiscountType(request.getDiscountType());
        voucher.setDiscountValue(request.getDiscountValue());
        voucher.setMinOrderAmount(request.getMinOrderAmount() != null
                ? request.getMinOrderAmount()
                : BigDecimal.ZERO);
        voucher.setMaxDiscountAmount(request.getMaxDiscountAmount());
        voucher.setStartAt(request.getStartAt());
        voucher.setEndAt(request.getEndAt());
        voucher.setStatus(request.getStatus());

        return voucherMapper.toResponse(voucherRepository.save(voucher));
    }

    @Override
    @Transactional(readOnly = true)
    public VoucherResponse getById(Long id) {
        return voucherMapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public VoucherResponse getByCode(String code) {
        Voucher voucher = voucherRepository.findByCode(code.trim().toUpperCase())
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
        return voucherMapper.toResponse(voucher);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VoucherResponse> search(String keyword, VoucherStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Voucher> result = voucherRepository.search(keyword, status, pageable);
        return result.map(voucherMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VoucherResponse> getActiveVouchers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("endAt").ascending());
        Page<Voucher> result = voucherRepository.findAllActive(LocalDateTime.now(), pageable);
        return result.map(voucherMapper::toResponse);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Voucher voucher = findById(id);
        if (voucherRepository.isVoucherUsed(id)) {
            throw new AppException(ErrorCode.VOUCHER_ALREADY_USED);
        }
        voucherRepository.delete(voucher);
    }

    @Override
    @Transactional
    public VoucherResponse toggleStatus(Long id) {
        Voucher voucher = findById(id);
        voucher.setStatus(voucher.getStatus() == VoucherStatus.ACTIVE
                ? VoucherStatus.INACTIVE
                : VoucherStatus.ACTIVE);
        return voucherMapper.toResponse(voucherRepository.save(voucher));
    }

    private Voucher findById(Long id) {
        return voucherRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
    }

    private void validateTimeRange(LocalDateTime startAt, LocalDateTime endAt) {
        if (endAt.isBefore(startAt)) {
            throw new AppException(ErrorCode.VOUCHER_INVALID_TIME_RANGE);
        }
    }

    private void validateDiscountValue(DiscountType type, BigDecimal value) {
        if (type == DiscountType.PERCENT && value.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new AppException(ErrorCode.VOUCHER_INVALID_PERCENT);
        }
    }
}