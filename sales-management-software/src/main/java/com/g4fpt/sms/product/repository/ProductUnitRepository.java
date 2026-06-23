package com.g4fpt.sms.product.repository;

import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductUnitRepository extends JpaRepository<ProductUnit, Long> {
    boolean existsBySkuIgnoreCase(String sku);
    boolean existsByBarcodeUnitIgnoreCase(String barcodeUnit);
    boolean existsBySkuIgnoreCaseAndIdNot(String sku, Long id);
    boolean existsByBarcodeUnitIgnoreCaseAndIdNot(String barcodeUnit, Long id);

    List<ProductUnit> findByProduct_Id(Long id);

    ProductUnit findByIdAndProduct_Id(Long id, Long productId);

    Optional<ProductUnit> findBySku(String sku);
    Optional<ProductUnit> findByBarcodeUnit(String barcodeUnit);

    List<ProductUnit> findByProduct_CategoryIdAndProduct_Status(
        Long categoryId, ProductStatus status);
    List<ProductUnit> findByProduct_Status(ProductStatus status);
}