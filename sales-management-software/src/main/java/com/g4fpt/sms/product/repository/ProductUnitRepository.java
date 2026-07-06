package com.g4fpt.sms.product.repository;

import com.g4fpt.sms.product.entity.Product;
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

    // Tìm ProductUnit theo sản phẩm + đơn vị (dùng khi người dùng chọn 2 ô riêng
    // biệt)
    Optional<ProductUnit> findByProduct_IdAndUnit_Id(Long productId, Long unitId);

    List<ProductUnit> findByProduct_CategoryIdAndProduct_Status(
            Long categoryId, ProductStatus status);

    List<ProductUnit> findByProduct_Status(ProductStatus status);
    @Query("""
            SELECT CASE WHEN COUNT(otd) > 0 THEN true ELSE false END
            FROM OrderTransactionDetail otd
            WHERE otd.productUnit.id = :productUnitId
    """)
    boolean existInOrderTransaction(@Param("productUnitId") Long productUnitId);

    // Lấy danh sách ProductUnit theo lịch sử nhập hàng của Nhà cung cấp
    @Query("SELECT DISTINCT d.productUnit FROM OrderTransactionDetail d " +
           "WHERE d.orderTransaction.supplier.id = :supplierId " +
           "AND d.orderTransaction.transactionType = 'IMPORT'")
    List<ProductUnit> findProductUnitsBySupplierImportHistory(@Param("supplierId") Long supplierId);

    @Query("SELECT pu.id AS id, pu.sku AS sku, pu.price AS price, pu.product AS product, pu.unit AS unit " +
           "FROM ProductUnit pu " +
           "WHERE pu.product.status = com.g4fpt.sms.product.enums.ProductStatus.ACTIVE " +
           "AND pu.product.category.status = com.g4fpt.sms.product.enums.CategoryStatus.ACTIVE " +
           "AND (:categoryId IS NULL OR pu.product.category.id = :categoryId) " +
           "AND (:keyword IS NULL OR :keyword = '' OR " +
           "     LOWER(pu.product.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "     LOWER(pu.sku) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<ProductUnitProjection> searchActiveProductUnits(
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword);
}
