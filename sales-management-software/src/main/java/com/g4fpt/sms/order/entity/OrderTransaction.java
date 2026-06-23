package com.g4fpt.sms.order.entity;

import com.g4fpt.sms.customer.entity.Customer;
import com.g4fpt.sms.supplier.entity.Supplier;
import com.g4fpt.sms.voucher.entity.Voucher;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordertransaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(name = "payment_method_id")
    private Long paymentMethodId;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "original_order_id")
    private Long originalOrderId;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "discount_amount", nullable = false)
    private BigDecimal discountAmount;

    @Column(name = "final_amount", nullable = false)
    private BigDecimal finalAmount;

    @Column(name = "paid_amount", nullable = false)
    private BigDecimal paidAmount;

    @Column(name = "change_amount", nullable = false)
    private BigDecimal changeAmount;

    @Column(nullable = false)
    private String status;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Column(name = "from_branch_id")
    private Long fromBranchId;

    @Column(name = "to_branch_id")
    private Long toBranchId;

    private String note;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "orderTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderTransactionDetail> details = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = "COMPLETED";
        if (this.transactionType == null) this.transactionType = "SALE";
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}