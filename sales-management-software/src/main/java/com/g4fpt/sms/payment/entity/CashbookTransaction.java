package com.g4fpt.sms.payment.entity;

import com.g4fpt.sms.branch.entity.Branch;
import com.g4fpt.sms.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cashbook_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashbookTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType; // IN or OUT

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod; // CASH or BANK

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "reference_code")
    private String referenceCode; // Order Code or Import Code

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Employee creator;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
