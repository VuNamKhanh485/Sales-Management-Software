package com.g4fpt.sms.product.repository;

import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT pu FROM ProductUnit pu LEFT JOIN FETCH pu.unit WHERE pu.product.id = :productId")
    List<ProductUnit> findByProductIdWithUnit(@Param("productId") Long productId);

    ProductUnit findByIdAndProduct_Id(Long id, Long productId);

    Optional<ProductUnit> findBySku(String sku);
    Optional<ProductUnit> findByBarcodeUnit(String barcodeUnit);

    List<ProductUnit> findByProduct_CategoryIdAndProduct_Status(
        Long categoryId, ProductStatus status);
    List<ProductUnit> findByProduct_Status(ProductStatus status);
    @Query("""
            SELECT CASE WHEN COUNT(otd) > 0 THEN true ELSE false END
            FROM OrderTransactionDetail otd
            WHERE otd.productUnit.id =: productUnitId
    """)
    boolean existInOrderTransaction(@Param("productUnitId") Long productUnitId);
}
