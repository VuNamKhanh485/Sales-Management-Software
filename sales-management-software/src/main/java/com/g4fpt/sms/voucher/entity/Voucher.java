package com.g4fpt.sms.voucher.entity;

import com.g4fpt.sms.employee.entity.Employee;
import com.g4fpt.sms.voucher.enums.VoucherStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Voucher")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "name_voucher", nullable = false)
    private String nameVoucher;

    @Column(name = "discount_type", nullable = false)
    private String discountType;

    @Column(name = "discount_value")
    private BigDecimal discountValue;

    @Column(name = "min_order_value")
    private BigDecimal minOrderValue;

    @Column(name = "max_discount_amount")
    private BigDecimal maxDiscountAmount;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "used_count")
    private Integer usedCount = 0;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private VoucherStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Employee createdBy;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.usedCount == null) this.usedCount = 0;
        if (this.status == null) this.status = VoucherStatus.ACTIVE;
    }
}