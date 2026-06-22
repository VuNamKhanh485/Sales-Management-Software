package com.g4fpt.sms.product.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * version 1
 * create: 30/05/2026
 * @author Nam Khanh
 */
@Entity
@Table(name = "ProductUnit")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class ProductUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @NotNull
    @Column(name = "conversion_value", nullable = false)
    @ToString.Include
    private Integer conventionValue;

    @NotNull
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    @ToString.Include
    private BigDecimal price;

    @Size(max = 255)
    @NotNull
    @Column(name = "barcode_unit", nullable = false)
    private String barcodeUnit;

    @Column(name = "is_base_unit")
    private Boolean isBaseUnit;

    @Size(max = 255)
    @NotNull
    @Column(name = "sku", nullable = false)
    private String sku;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}


