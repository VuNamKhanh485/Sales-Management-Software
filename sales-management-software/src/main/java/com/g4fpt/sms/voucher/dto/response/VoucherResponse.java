package com.g4fpt.sms.voucher.dto.response;

import com.g4fpt.sms.voucher.enums.VoucherStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VoucherResponse {

    private Long id;
    private String code;
    private String nameVoucher;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscountAmount;
    private Integer usageLimit;
    private Integer usedCount;
    private LocalDate startDate;
    private LocalDate endDate;
    private VoucherStatus status;
    private LocalDateTime createdAt;
    private String createdByName; // Employee.fullname
}