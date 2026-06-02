package com.g4fpt.sms.product.entity;

import com.g4fpt.sms.product.enums.CategoryStatus;
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
@Table(name = "category")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(unique = true, nullable = false)
    @ToString.Include
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column (nullable = false)
    private CategoryStatus status;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    private LocalDateTime updateDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
    private List<Product> products;

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }
}
