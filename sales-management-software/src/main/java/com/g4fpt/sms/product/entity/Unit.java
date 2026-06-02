package com.g4fpt.sms.product.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * version 1
 * create: 30/05/2026
 * @author Nam Khanh
 */
@Entity
@Table(name = "unit")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    @ToString.Include
    private String name;


    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdDate;

    @OneToMany(mappedBy = "unit")
    private List<ProductUnit> productunits;

    @PrePersist
    public void prePersist() {
        createdDate = LocalDateTime.now();
    }
}
