package com.g4fpt.sms.product.repository;

import com.g4fpt.sms.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
