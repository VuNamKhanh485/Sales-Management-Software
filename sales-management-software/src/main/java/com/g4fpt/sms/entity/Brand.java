package com.g4fpt.sms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
@ToString
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String brandName;

    @Column(nullable = false)
    private String createdDate;

    @OneToMany (fetch = FetchType.LAZY, mappedBy = "brand")
    private List<Product> products;
    @PrePersist
    public void prePersist() {
        createdDate = LocalDate.now().toString();
    }
}