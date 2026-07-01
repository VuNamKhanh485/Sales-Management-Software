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

    ProductUnit findByIdAndProduct_Id(Long id, Long productId);

    Optional<ProductUnit> findBySku(String sku);

    Optional<ProductUnit> findByBarcodeUnit(String barcodeUnit);

    // Tìm ProductUnit theo sản phẩm + đơn vị (dùng khi người dùng chọn 2 ô riêng
    // biệt)
    Optional<ProductUnit> findByProduct_IdAndUnit_Id(Long productId, Long unitId);

    List<ProductUnit> findByProduct_CategoryIdAndProduct_Status(
            Long categoryId, ProductStatus status);

    List<ProductUnit> findByProduct_Status(ProductStatus status);

    // Lấy danh sách ProductUnit theo lịch sử nhập hàng của Nhà cung cấp
    @Query("SELECT DISTINCT d.productUnit FROM OrderTransactionDetail d " +
           "WHERE d.orderTransaction.supplier.id = :supplierId " +
           "AND d.orderTransaction.transactionType = 'IMPORT'")
    List<ProductUnit> findProductUnitsBySupplierImportHistory(@Param("supplierId") Long supplierId);
}
