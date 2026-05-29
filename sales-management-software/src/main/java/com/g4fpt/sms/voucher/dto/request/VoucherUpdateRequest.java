package com.g4fpt.sms.voucher.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VoucherUpdateRequest {

    @NotBlank(message = "Mã voucher không được để trống")
    @Size(max = 255)
    private String code;

    @NotBlank(message = "Tên voucher không được để trống")
    private String nameVoucher;

    @NotBlank(message = "Loại giảm giá không được để trống")
    private String discountType;

    @NotNull(message = "Giá trị giảm không được để trống")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal discountValue;

    private BigDecimal minOrderValue;
    private BigDecimal maxDiscountAmount;

    @Min(value = 1)
    private Integer usageLimit;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate endDate;

    private String status;
}