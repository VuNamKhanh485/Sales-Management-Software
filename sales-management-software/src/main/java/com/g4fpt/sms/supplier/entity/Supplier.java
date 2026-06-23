package com.g4fpt.sms.supplier.entity;

import com.g4fpt.sms.supplier.enums.SupplierStatus;
import com.g4fpt.sms.order.entity.OrderTransaction;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Supplier")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(nullable = false, unique = true)
    @ToString.Include
    private String code;

    @ToString.Include
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @ToString.Include
    private String phone;

    @Column(nullable = false)
    @ToString.Include
    private String email;

    @Column(nullable = false)
    @ToString.Include
    private String address;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SupplierStatus status;

    private String note;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "supplier")
    List<OrderTransaction> orderTransactionList;
}
