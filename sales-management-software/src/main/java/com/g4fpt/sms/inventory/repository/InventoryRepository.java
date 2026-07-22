package com.g4fpt.sms.inventory.repository;

import com.g4fpt.sms.inventory.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Đếm số lượng SẢN PHẨM (Product) khác nhau trong kho của một chi nhánh
    @Query("SELECT COUNT(DISTINCT i.productUnit.product.id) FROM Inventory i WHERE i.branch.id = :branchId")
    long countDistinctProductByBranchId(@Param("branchId") Long branchId);

    // Lấy toàn bộ mặt hàng trong kho của một chi nhánh (có phân trang)
    Page<Inventory> findByBranchId(Long branchId, Pageable pageable);

    // Lấy toàn bộ mặt hàng trong kho của một chi nhánh (không phân trang)
    List<Inventory> findAllByBranchId(Long branchId);

    // Kiểm tra xem mặt hàng đã tồn tại trong kho của chi nhánh này chưa
    boolean existsByBranchIdAndProductUnitId(Long branchId, Long productUnitId);

    // Tìm kiếm bản ghi Inventory theo branchId và productUnitId
    Optional<Inventory> findByBranchIdAndProductUnitId(Long branchId, Long productUnitId);


    // Tìm kiếm theo tên sản phẩm HOẶC SKU (không phân biệt hoa/thường)
    @Query("SELECT i FROM Inventory i " +
            "WHERE i.branch.id = :branchId " +
            "AND (LOWER(i.productUnit.product.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "  OR LOWER(i.productUnit.sku) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Inventory> searchByBranchIdAndKeyword(@Param("branchId") Long branchId,
                                               @Param("keyword") String keyword,
                                               Pageable pageable);

    // Lọc hàng sắp hết (stock <= minStock) trong một chi nhánh
    @Query("SELECT i FROM Inventory i " +
            "WHERE i.branch.id = :branchId " +
            "AND i.stock <= i.minStock")
    Page<Inventory> findLowStockByBranchId(@Param("branchId") Long branchId, Pageable pageable);

    boolean existsByProductUnitId(Long productUnitId);

    boolean existsByBranchIdAndStockGreaterThan(Long branchId, Integer stock);
}
