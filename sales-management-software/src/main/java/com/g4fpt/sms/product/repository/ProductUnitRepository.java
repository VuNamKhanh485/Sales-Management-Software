package com.g4fpt.sms.product.repository;

import com.g4fpt.sms.product.entity.ProductUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductUnitRepository extends JpaRepository<ProductUnit, Long> {
    boolean existsBySkuIgnoreCase(String sku);

    boolean existsByBarcodeUnitIgnoreCase(String barcodeUnit);

    List<ProductUnit> findByProduct_Id(Long id);
}
