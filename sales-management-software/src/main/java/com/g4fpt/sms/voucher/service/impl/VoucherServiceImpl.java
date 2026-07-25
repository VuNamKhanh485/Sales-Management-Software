package com.g4fpt.sms.voucher.service.impl;

import com.g4fpt.sms.common.exception.AppException;
import com.g4fpt.sms.common.exception.ErrorCode;
import com.g4fpt.sms.voucher.dto.VoucherDTO;
import com.g4fpt.sms.voucher.entity.Voucher;
import com.g4fpt.sms.voucher.enums.DiscountType;
import com.g4fpt.sms.voucher.enums.VoucherStatus;
import com.g4fpt.sms.order.repository.OrderTransactionRepository;
import com.g4fpt.sms.customer.entity.CustomerRank;
import com.g4fpt.sms.customer.repository.CustomerRankRepository;
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
    private final OrderTransactionRepository orderTransactionRepository;
    private final CustomerRankRepository customerRankRepository;

    @Override
    @Transactional
    public VoucherDTO create(VoucherDTO request) {
        java.util.Optional<Voucher> existing = voucherRepository.findByCode(request.getCode().trim().toUpperCase());
        if (existing.isPresent()) {
            return update(existing.get().getId(), request);
        }

        validateTimeRange(request.getStartAt(), request.getEndAt());
        validateDiscountValue(request.getDiscountType(), request.getDiscountValue());

        if (request.getStartAt().toLocalDate().isBefore(java.time.LocalDate.now())) {
            throw new AppException(ErrorCode.VOUCHER_START_DATE_PAST);
        }

        CustomerRank rank = null;
        if (request.getCustomerRankId() != null) {
            rank = customerRankRepository.findById(request.getCustomerRankId()).orElse(null);
        }
        Voucher voucher = VoucherDTO.toEntity(request, rank);
        
        return VoucherDTO.fromEntity(voucherRepository.save(voucher));
    }

    @Override
    @Transactional
    public VoucherDTO update(Long id, VoucherDTO request) {
        Voucher voucher = findById(id);

        validateTimeRange(request.getStartAt(), request.getEndAt());
        validateDiscountValue(request.getDiscountType(), request.getDiscountValue());
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

        if (request.getCustomerRankId() != null) {
            voucher.setCustomerRank(customerRankRepository.findById(request.getCustomerRankId()).orElse(null));
        } else {
            voucher.setCustomerRank(null);
        }

        return VoucherDTO.fromEntity(voucherRepository.save(voucher));
    }

    @Override
    @Transactional(readOnly = true)
    public VoucherDTO getById(Long id) {
        return VoucherDTO.fromEntity(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public VoucherDTO getByCode(String code) {
        Voucher voucher = voucherRepository.findByCode(code.trim().toUpperCase())
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
        return VoucherDTO.fromEntity(voucher);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VoucherDTO> search(String keyword, VoucherStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Voucher> result;

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        String kw = hasKeyword ? keyword.trim() : null;

        if (hasKeyword && status != null) {
            result = voucherRepository.findByStatusAndCodeContainingIgnoreCaseOrStatusAndNameContainingIgnoreCase(
                    status, kw, status, kw, pageable);
        } else if (hasKeyword) {
            result = voucherRepository.findByCodeContainingIgnoreCaseOrNameContainingIgnoreCase(
                    kw, kw, pageable);
        } else if (status != null) {
            result = voucherRepository.findByStatus(status, pageable);
        } else {
            result = voucherRepository.findAll(pageable);
        }

        return result.map(VoucherDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VoucherDTO> getActiveVouchers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("endAt").ascending());
        LocalDateTime now = LocalDateTime.now();
        Page<Voucher> result = voucherRepository.findByStatusAndStartAtLessThanEqualAndEndAtGreaterThanEqual(
                VoucherStatus.ACTIVE, now, now, pageable);
        return result.map(VoucherDTO::fromEntity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Voucher voucher = findById(id);
        if (orderTransactionRepository.existsByVoucherId(id)) {
            throw new AppException(ErrorCode.VOUCHER_ALREADY_USED);
        }
        voucherRepository.delete(voucher);
    }

    @Override
    @Transactional
    public VoucherDTO toggleStatus(Long id) {
        Voucher voucher = findById(id);
        voucher.setStatus(voucher.getStatus() == VoucherStatus.ACTIVE
                ? VoucherStatus.INACTIVE
                : VoucherStatus.ACTIVE);
        return VoucherDTO.fromEntity(voucherRepository.save(voucher));
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