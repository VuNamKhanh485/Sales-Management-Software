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
@ToString
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @ToString.Exclude
    @Column(name = "created_at")
    private LocalDateTime createdDate;

    @OneToMany(mappedBy = "unit")
    @ToString.Exclude
    private List<ProductUnit> productunits;

    @PrePersist
    public void prePersist() {
        createdDate = LocalDateTime.now();
    }
}
