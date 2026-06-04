package com.g4fpt.sms.product.service;

import com.g4fpt.sms.product.dto.request.ProductUnitRequest;
import com.g4fpt.sms.product.entity.ProductUnit;

import java.util.List;

public interface ProductUnitService {
    public List<ProductUnit> findAll();
    public ProductUnit create(ProductUnitRequest productUnitRequest);
    public ProductUnit update(Long id, ProductUnitRequest productUnitRequest);
    public void delete(Long id);

    ProductUnit findById(Long id);
}
