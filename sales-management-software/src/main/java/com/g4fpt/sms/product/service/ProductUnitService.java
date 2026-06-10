package com.g4fpt.sms.product.service;

import com.g4fpt.sms.product.dto.request.BrandRequest;
import com.g4fpt.sms.product.dto.request.ProductUnitRequest;
import com.g4fpt.sms.product.dto.response.ProductUnitResponse;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.util.ValidationError;

import java.util.List;

public interface ProductUnitService {
    public void create(ProductUnitRequest productUnitRequest);
    public void update(Long id, ProductUnitRequest productUnitRequest);
    public void deleteById(Long id);
    public List<ProductUnitResponse> findAll();
    ProductUnitResponse findById(Long id);
    void validate(ProductUnitRequest productUnitRequest);
}
