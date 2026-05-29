package com.g4fpt.sms.voucher.mapper;

import com.g4fpt.sms.voucher.dto.request.VoucherCreateRequest;
import com.g4fpt.sms.voucher.dto.request.VoucherUpdateRequest;
import com.g4fpt.sms.voucher.dto.response.VoucherResponse;
import com.g4fpt.sms.voucher.entity.Voucher;
import com.g4fpt.sms.voucher.enums.VoucherStatus;
import org.springframework.stereotype.Component;

@Component
public class VoucherMapper {

    public Voucher toEntity(VoucherCreateRequest req) {
        return Voucher.builder()
                .code(req.getCode().toUpperCase().trim())
                .nameVoucher(req.getNameVoucher())
                .discountType(req.getDiscountType())
                .discountValue(req.getDiscountValue())
                .minOrderValue(req.getMinOrderValue())
                .maxDiscountAmount(req.getMaxDiscountAmount())
                .usageLimit(req.getUsageLimit())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .status(parseStatus(req.getStatus()))
                .build();
    }

    public void updateEntity(Voucher voucher, VoucherUpdateRequest req) {
        voucher.setCode(req.getCode().toUpperCase().trim());
        voucher.setNameVoucher(req.getNameVoucher());
        voucher.setDiscountType(req.getDiscountType());
        voucher.setDiscountValue(req.getDiscountValue());
        voucher.setMinOrderValue(req.getMinOrderValue());
        voucher.setMaxDiscountAmount(req.getMaxDiscountAmount());
        voucher.setUsageLimit(req.getUsageLimit());
        voucher.setStartDate(req.getStartDate());
        voucher.setEndDate(req.getEndDate());
        voucher.setStatus(parseStatus(req.getStatus()));
    }

    public VoucherResponse toResponse(Voucher v) {
        return VoucherResponse.builder()
                .id(v.getId())
                .code(v.getCode())
                .nameVoucher(v.getNameVoucher())
                .discountType(v.getDiscountType())
                .discountValue(v.getDiscountValue())
                .minOrderValue(v.getMinOrderValue())
                .maxDiscountAmount(v.getMaxDiscountAmount())
                .usageLimit(v.getUsageLimit())
                .usedCount(v.getUsedCount())
                .startDate(v.getStartDate())
                .endDate(v.getEndDate())
                .status(v.getStatus())
                .createdAt(v.getCreatedAt())
                .createdByName(v.getCreatedBy() != null ? v.getCreatedBy().getFullname() : null)
                .build();
    }

    private VoucherStatus parseStatus(String status) {
        if (status == null || status.isBlank()) return VoucherStatus.ACTIVE;
        try {
            return VoucherStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return VoucherStatus.ACTIVE;
        }
    }
}