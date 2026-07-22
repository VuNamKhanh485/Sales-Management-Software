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

    // Lấy toàn bộ mặt hàng trong kho của một chi nhánh
    Page<Inventory> findByBranchId(Long branchId, Pageable pageable);

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

    /**
     * Filter dùng cho inventory report
     * @param branchId tìm theo id của chi nhánh có thể để trống
     * @param categoryId tìm theo id của danh mục có thể để trống
     * @param brandId tìm theo id của nhãn hàng có thể để trống
     * @param keyword tìm theo từ khóa có thể để trống
     * @return Danh sách kho trùng với các biến yêu cầu trên
     */
    @Query("SELECT i FROM Inventory i " +
            "JOIN FETCH i.branch b " +
            "JOIN FETCH i.productUnit pu " +
            "JOIN FETCH pu.product p " +
            "JOIN FETCH pu.unit u " +
            "WHERE (:branchId IS NULL OR i.branch.id = :branchId) " +
            "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:brandId IS NULL OR p.brand.id = :brandId) " +
            "AND (:keyword IS NULL OR pu.sku LIKE %:keyword% OR p.name LIKE %:keyword%)")
    List<Inventory> findByFilter(
            @Param("branchId") Long branchId,
            @Param("categoryId") Long categoryId,
            @Param("brandId") Long brandId,
            @Param("keyword") String keyword
    );


}
