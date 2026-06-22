package com.g4fpt.sms.product.entity;

import com.g4fpt.sms.branch.entity.Branch;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Inventory")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_unit_id")
    private ProductUnit productUnit;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "min_stock", nullable = false)
    private Integer minStock;

    @Column(name = "max_stock", nullable = false)
    private Integer maxStock;

    @Column(name = "position_in_shop")
    private String positionInShop;
}
