package com.g4fpt.sms.customer.entity;

import com.g4fpt.sms.customer.enums.CustomerStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_rank_id")
    private CustomerRank customerRank;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "customer_code", nullable = false, unique = true)
    private String customerCode;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String phone;

    private String email;

    private String address;

    @Column(name = "total_point", nullable = false)
    private Integer totalPoint;

    @Column(name = "used_point", nullable = false)
    private Integer usedPoint;

    @Column(name = "total_revenue", nullable = false)
    private BigDecimal totalRevenue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerStatus status;

    private String note;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = CustomerStatus.ACTIVE;
        if (this.totalPoint == null) this.totalPoint = 0;
        if (this.usedPoint == null) this.usedPoint = 0;
        if (this.totalRevenue == null) this.totalRevenue = BigDecimal.ZERO;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}