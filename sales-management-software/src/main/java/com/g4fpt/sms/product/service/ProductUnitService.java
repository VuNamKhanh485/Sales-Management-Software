package com.g4fpt.sms.product.service;

import com.g4fpt.sms.product.dto.request.ProductUnitRequest;
import com.g4fpt.sms.product.dto.response.ProductUnitResponse;
import com.g4fpt.sms.product.entity.Product;
import com.g4fpt.sms.product.entity.ProductUnit;
import java.util.List;

public interface ProductUnitService {
    ProductUnit create(ProductUnitRequest productUnitRequest, Product product);
    ProductUnit update(ProductUnitRequest productUnitRequest, Product product);
    void deleteById(Long id);
    List<ProductUnitResponse> findAll();
    ProductUnitResponse findById(Long id);
    List<ProductUnitResponse> findByProductId(Long id);
    void validate(ProductUnitRequest productUnitRequest, Long excludeId);
}
