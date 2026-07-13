package com.g4fpt.sms.product.repository;

import com.g4fpt.sms.product.entity.Category;
import com.g4fpt.sms.product.enums.CategoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);
    @Query("""
            SELECT CASE WHEN COUNT(otd) > 0 THEN true ELSE false END
            FROM OrderTransactionDetail otd
            WHERE otd.productUnit.product.category.id = :categoryId
    """)
    boolean existInOrderTransaction(@Param("categoryId") Long categoryId);

    List<Category> findAllActive(CategoryStatus status);
}
