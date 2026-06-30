package com.g4fpt.sms.voucher.dto.request;

import com.g4fpt.sms.voucher.enums.DiscountType;
import com.g4fpt.sms.voucher.enums.VoucherStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherRequest {

    @NotBlank(message = "Code không được để trống")
    @Size(max = 100, message = "Code không được vượt quá 100 ký tự")
    private String code;

    @NotBlank(message = "Tên voucher không được để trống")
    @Size(max = 255, message = "Tên không được vượt quá 255 ký tự")
    private String name;

    @NotNull(message = "Loại giảm giá không được để trống")
    private DiscountType discountType;

    @NotNull(message = "Giá trị giảm không được để trống")
    @DecimalMin(value = "0", message = "Giá trị giảm phải >= 0")
    private BigDecimal discountValue;

    @DecimalMin(value = "0", message = "Giá trị đơn tối thiểu phải >= 0")
    private BigDecimal minOrderAmount;

    @DecimalMin(value = "0", message = "Giảm tối đa phải >= 0")
    private BigDecimal maxDiscountAmount;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    @FutureOrPresent(message = "Thời gian bắt đầu không được trước hiện tại")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startAt;

    @NotNull(message = "Thời gian kết thúc không được để trống")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endAt;

    @NotNull(message = "Trạng thái không được để trống")
    @Builder.Default
    private VoucherStatus status = VoucherStatus.ACTIVE;
}
