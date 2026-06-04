package com.g4fpt.sms.product.service;

import com.g4fpt.sms.product.dto.request.ProductRequest;
import com.g4fpt.sms.product.entity.Product;

import java.util.List;

public interface ProductService {
    Product create(ProductRequest productRequest);
    Product update(long id, ProductRequest productRequest);
    Product findById(long id);
    List<Product> findByName(String name);
    List<Product> findByBrand(Long brandId);
    List<Product> findByCategory(Long categoryId);
    void delete(long id);
    List<Product> getAll();
}
