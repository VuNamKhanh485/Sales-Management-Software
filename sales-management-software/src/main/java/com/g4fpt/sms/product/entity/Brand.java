package com.g4fpt.sms.product.entity;

import jakarta.persistence.*;
import lombok.*;

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
@ToString(of = {"id", "name"})
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String createdDate;

    @OneToMany (fetch = FetchType.LAZY, mappedBy = "brand")
    private List<Product> products;

    @PrePersist
    public void prePersist() {
        createdDate = LocalDate.now().toString();
    }
}