package com.g4fpt.sms.product.service;

import com.g4fpt.sms.product.entity.Product;

import java.util.List;

public interface ProductService {
    Product create(Product product);
    Product update(Product product);
    Product findById(long id);
    Product findByName(String name);
    Product findByBrand(String brand);
    void delete(long id);
    List<Product> getAll();
}
