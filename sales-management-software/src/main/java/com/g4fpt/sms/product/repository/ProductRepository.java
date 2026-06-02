package com.g4fpt.sms.product.repository;

import com.g4fpt.sms.product.entity.Brand;
import com.g4fpt.sms.product.entity.Category;
import com.g4fpt.sms.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByBrand(Long brandId);

    List<Product> findByCategory(Long categoryId);

    List<Product> findByNameContainingIgnoreCase(String name);

}
