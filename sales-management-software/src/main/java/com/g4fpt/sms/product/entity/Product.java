package com.g4fpt.sms.product.entity;

import com.g4fpt.sms.product.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * version 1
 * create: 27/05/2026
 * @author Nam Khanh
 */

@Entity
@Table(name = "product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column(nullable = false, unique = true)
    @ToString.Include
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    private String note;

    private LocalDateTime updatedDate;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @OneToMany(mappedBy = "product",
                cascade = CascadeType.ALL)
    private List<ProductUnit> productunits;

    @PrePersist
    public void prePersist() {
        createdDate = LocalDateTime.now();
    }
}
