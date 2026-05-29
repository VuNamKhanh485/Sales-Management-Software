package com.g4fpt.sms.product.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

/**
 * version 1
 * create: 30/05/2026
 * @author Nam Khanh
 */
@Entity
@Table(name = "productunit")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @NotNull
    @ToString.Exclude
    @Column(name = "convention_value", nullable = false)
    private Integer conventionValue;

    @NotNull
    @ToString.Exclude
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Size(max = 255)
    @NotNull
    @ToString.Exclude
    @Column(name = "barcode_unit", nullable = false)
    private String barcodeUnit;

    @ToString.Exclude
    @Column(name = "is_base_unit")
    private Boolean isBaseUnit;

    @Size(max = 255)
    @NotNull
    @ToString.Exclude
    @Column(name = "sku", nullable = false)
    private String sku;


}


