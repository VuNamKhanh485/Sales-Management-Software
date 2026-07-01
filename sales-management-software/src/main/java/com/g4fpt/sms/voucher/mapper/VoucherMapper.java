package com.g4fpt.sms.voucher.mapper;

import com.g4fpt.sms.voucher.dto.request.VoucherRequest;
import com.g4fpt.sms.voucher.dto.response.VoucherResponse;
import com.g4fpt.sms.voucher.entity.Voucher;
import com.g4fpt.sms.voucher.enums.VoucherStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class VoucherMapper {

    public Voucher toEntity(VoucherRequest request) {
        if (request == null) {
            return null;
        }
        return Voucher.builder()
                .code(request.getCode().trim().toUpperCase())
                .name(request.getName().trim())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minOrderAmount(request.getMinOrderAmount() != null
                        ? request.getMinOrderAmount()
                        : BigDecimal.ZERO)
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .status(request.getStatus() != null ? request.getStatus() : VoucherStatus.ACTIVE)
                .build();
    }

    public VoucherResponse toResponse(Voucher voucher) {
        if (voucher == null) {
            return null;
        }
        return VoucherResponse.builder()
                .id(voucher.getId())
                .code(voucher.getCode())
                .name(voucher.getName())
                .discountType(voucher.getDiscountType())
                .discountValue(voucher.getDiscountValue())
                .minOrderAmount(voucher.getMinOrderAmount())
                .maxDiscountAmount(voucher.getMaxDiscountAmount())
                .startAt(voucher.getStartAt())
                .endAt(voucher.getEndAt())
                .status(voucher.getStatus())
                .createdAt(voucher.getCreatedAt())
                .updatedAt(voucher.getUpdatedAt())
                .customerRankId(voucher.getCustomerRank() != null ? voucher.getCustomerRank().getId() : null)
                .customerRankName(voucher.getCustomerRank() != null ? voucher.getCustomerRank().getName() : null)
                .build();
    }
}