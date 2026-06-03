package com.g4fpt.sms.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * version 1
 * create: 27/05/2026
 * @author Nam Khanh
 */
@Entity
@Table(name = "brand")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(nullable = false, unique = true)
    @ToString.Include
    private String name;

    @Column(nullable = false)
    private String createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany (fetch = FetchType.LAZY, mappedBy = "brand")
    private List<Product> products;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDate.now().toString();
    }
}