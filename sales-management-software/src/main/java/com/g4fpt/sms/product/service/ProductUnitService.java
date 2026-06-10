package com.g4fpt.sms.product.service;

import com.g4fpt.sms.product.dto.request.BrandRequest;
import com.g4fpt.sms.product.dto.request.ProductUnitRequest;
import com.g4fpt.sms.product.dto.response.ProductUnitResponse;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.util.ValidationError;

import java.util.List;

public interface ProductUnitService {
    void create(ProductUnitRequest productUnitRequest);
    void update(Long id, ProductUnitRequest productUnitRequest);
    void deleteById(Long id);
    List<ProductUnitResponse> findAll();
    ProductUnitResponse findById(Long id);
    List<ProductUnitResponse> findByProductId(Long id);
    void validate(ProductUnitRequest productUnitRequest, Long excludeId);
}
