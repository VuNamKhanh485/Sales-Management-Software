package com.g4fpt.sms.product.entity;

import com.g4fpt.sms.product.enums.CategoryStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * version 1
 * create: 27/05/2026
 * @author Nam Khanh
 */

@Entity
@Table(name = "Category")
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

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updateAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
    private List<Product> products;

}
