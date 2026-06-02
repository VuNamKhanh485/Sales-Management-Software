package com.g4fpt.sms.product.service.impl;

import com.g4fpt.sms.product.dto.ProductRequest;
import com.g4fpt.sms.product.dto.ProductUnitRequest;
import com.g4fpt.sms.product.entity.Product;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.repository.ProductRepository;
import com.g4fpt.sms.product.service.ProductService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product create(ProductRequest productRequest) {
        Product product = new Product();

        for(ProductUnitRequest productUnitRequest : productRequest.getProductUnitsRequest()){
            ProductUnit productUnit = new ProductUnit();

            productUnit.setConventionValue(productUnitRequest.getConventionValue());
            productUnit.setPrice(productUnitRequest.getUnitPrice());
            productUnit.setBarcodeUnit(productUnitRequest.getBarcodeUnit());
            productUnit.setIsBaseUnit(productUnitRequest.getIsBaseUnit());
            productUnit.setSku(productUnitRequest.getSku());

            product.getProductunits().add(productUnit);
        }

        product.setCategory(productRequest.getCategory());
        product.setBrand(productRequest.getBrand());
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setStatus(productRequest.getStatus());
        product.setNote(productRequest.getNote());

        product.setCreatedDate(LocalDateTime.now());

        return productRepository.save(product);
    }

    @Override
    public Product update(long id, ProductRequest productRequest) {
        Product product = findById(id);

        if(product != null){
            product.setCategory(productRequest.getCategory());
            product.setBrand(productRequest.getBrand());
            product.setName(productRequest.getName());
            product.setDescription(productRequest.getDescription());
            product.setStatus(productRequest.getStatus());
            product.setNote(productRequest.getNote());

            product.setUpdatedDate(LocalDateTime.now());
            return productRepository.save(product);
        }
        return null;
    }

    @Override
    public Product findById(long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public List<Product> findByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Product> findByBrand(Long brandId) {
        return productRepository.findByBrand(brandId);
    }

    @Override
    public List<Product> findByCategory(Long categoryId) {
        return productRepository.findByCategory(categoryId);
    }

    @Override
    public void delete(long id) {

    }

    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }
}
