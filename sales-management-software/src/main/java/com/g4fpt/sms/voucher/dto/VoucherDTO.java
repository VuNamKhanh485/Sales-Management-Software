package com.g4fpt.sms.voucher.dto;

import com.g4fpt.sms.customer.entity.CustomerRank;
import com.g4fpt.sms.voucher.entity.Voucher;
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
public class VoucherDTO {

    private Long id;

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

    private Long customerRankId;
    private String customerRankName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static VoucherDTO fromEntity(Voucher entity) {
        if (entity == null) return null;
        
        return VoucherDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .discountType(entity.getDiscountType())
                .discountValue(entity.getDiscountValue())
                .minOrderAmount(entity.getMinOrderAmount())
                .maxDiscountAmount(entity.getMaxDiscountAmount())
                .startAt(entity.getStartAt())
                .endAt(entity.getEndAt())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .customerRankId(entity.getCustomerRank() != null ? entity.getCustomerRank().getId() : null)
                .customerRankName(entity.getCustomerRank() != null ? entity.getCustomerRank().getName() : null)
                .build();
    }

    public static Voucher toEntity(VoucherDTO dto, CustomerRank rank) {
        if (dto == null) return null;
        
        Voucher voucher = new Voucher();
        voucher.setId(dto.getId());
        voucher.setCode(dto.getCode());
        voucher.setName(dto.getName());
        voucher.setDiscountType(dto.getDiscountType());
        voucher.setDiscountValue(dto.getDiscountValue());
        voucher.setMinOrderAmount(dto.getMinOrderAmount() != null ? dto.getMinOrderAmount() : BigDecimal.ZERO);
        voucher.setMaxDiscountAmount(dto.getMaxDiscountAmount() != null ? dto.getMaxDiscountAmount() : BigDecimal.ZERO);
        voucher.setStartAt(dto.getStartAt());
        voucher.setEndAt(dto.getEndAt());
        voucher.setStatus(dto.getStatus() != null ? dto.getStatus() : VoucherStatus.ACTIVE);
        voucher.setCustomerRank(rank);
        return voucher;
    }
}
