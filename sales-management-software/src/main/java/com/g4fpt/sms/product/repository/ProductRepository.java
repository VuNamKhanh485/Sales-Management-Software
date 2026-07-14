package com.g4fpt.sms.product.repository;

import com.g4fpt.sms.product.entity.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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


}
