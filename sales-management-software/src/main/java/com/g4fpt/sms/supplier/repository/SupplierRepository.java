package com.g4fpt.sms.supplier.repository;

import com.g4fpt.sms.supplier.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    boolean existsByCodeIgnoreCase(String code);
    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);
    Page<Supplier> findByNameContainingIgnoreCase(String name, Pageable pageable);

}
