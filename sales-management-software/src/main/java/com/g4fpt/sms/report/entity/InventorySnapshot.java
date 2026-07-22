package com.g4fpt.sms.report.entity;

import com.g4fpt.sms.branch.entity.Branch;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.report.emuns.SnapshotType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventorysnapshot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventorySnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "snapshot_type", nullable = false)
    private SnapshotType snapshotType;

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;

    @Column(name = "branch_id", insertable = false, updatable = false)
    private Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "product_unit_id", insertable = false, updatable = false)
    private Long productUnitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_unit_id", nullable = false)
    private ProductUnit productUnit;

    @Column(name = "opening_stock", nullable = false)
    private Integer openingStock;

    @Column(name = "opening_value", nullable = false, precision = 18, scale = 2)
    private BigDecimal openingValue;

    @Column(name = "stock_in", nullable = false)
    private Integer stockIn;

    @Column(name = "stock_in_value", nullable = false, precision = 18, scale = 2)
    private BigDecimal stockInValue;

    @Column(name = "stock_out", nullable = false)
    private Integer stockOut;

    @Column(name = "stock_out_value", nullable = false, precision = 18, scale = 2)
    private BigDecimal stockOutValue;

    @Column(name = "closing_stock", nullable = false)
    private Integer closingStock;

    @Column(name = "closing_value", nullable = false, precision = 18, scale = 2)
    private BigDecimal closingValue;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
