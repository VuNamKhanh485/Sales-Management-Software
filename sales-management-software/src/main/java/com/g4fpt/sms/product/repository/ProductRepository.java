package com.g4fpt.sms.product.repository;

import com.g4fpt.sms.product.dto.request.ProductFilterRequest;
import com.g4fpt.sms.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>,
          JpaSpecificationExecutor<Product> {
     List<Product> findByBrand_Id(Long brandId);

     List<Product> findByCategory_Id(Long categoryId);

     List<Product> findByNameContainingIgnoreCase(String name);

     boolean existsByNameIgnoreCase(String name);

     boolean existsByNameIgnoreCaseAndIdNot(String name, Long excludeId);

     @Query("""
                       SELECT CASE WHEN COUNT(otd) > 0 THEN true ELSE false END
                       FROM OrderTransactionDetail otd
                       WHERE otd.productUnit.product.id = :productId
               """)
     boolean existInOrderTransaction(@Param("productId") Long productId);

     @Query("""
               SELECT p
               FROM Product p
               JOIN p.productUnits pu
               WHERE pu.isBaseUnit = true
               AND (:#{#filter.keyword} IS NULL
                    OR LOWER(p.name) LIKE LOWER(CONCAT('%', :#{#filter.keyword}, '%')))
               AND (:#{#filter.brandId} IS NULL
                    OR p.brand.id = :#{#filter.brandId})
               AND (:#{#filter.categoryId} IS NULL
                    OR p.category.id = :#{#filter.categoryId})
               AND (:#{#filter.status} IS NULL
                    OR p.status = :#{#filter.status})
               ORDER BY pu.sku ASC
               """)
     Page<Product> findAllOrderByBaseSkuAsc(
               @Param("filter") ProductFilterRequest filter,
               Pageable pageable);

     @Query("""
               SELECT p
               FROM Product p
               JOIN p.productUnits pu
               WHERE pu.isBaseUnit = true
               AND (:#{#filter.keyword} IS NULL
                    OR LOWER(p.name) LIKE LOWER(CONCAT('%', :#{#filter.keyword}, '%')))
               AND (:#{#filter.brandId} IS NULL
                    OR p.brand.id = :#{#filter.brandId})
               AND (:#{#filter.categoryId} IS NULL
                    OR p.category.id = :#{#filter.categoryId})
               AND (:#{#filter.status} IS NULL
                    OR p.status = :#{#filter.status})
               ORDER BY pu.sku DESC
               """)
     Page<Product> findAllOrderByBaseSkuDesc(
               @Param("filter") ProductFilterRequest filter,
               Pageable pageable);
}
