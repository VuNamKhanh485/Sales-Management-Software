package com.g4fpt.sms.voucher.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VoucherCreateRequest {

    @NotBlank(message = "Mã voucher không được để trống")
    @Size(max = 255, message = "Mã voucher không quá 255 ký tự")
    private String code;

    @NotBlank(message = "Tên voucher không được để trống")
    private String nameVoucher;

    @NotBlank(message = "Loại giảm giá không được để trống")
    private String discountType; // PERCENT | FIXED

    @NotNull(message = "Giá trị giảm không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá trị giảm phải lớn hơn 0")
    private BigDecimal discountValue;

    private BigDecimal minOrderValue;
    private BigDecimal maxDiscountAmount;

    @Min(value = 1, message = "Giới hạn sử dụng phải lớn hơn 0")
    private Integer usageLimit;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate endDate;

    private String status;
}