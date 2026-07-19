package com.g4fpt.sms.product.service;

import com.g4fpt.sms.product.dto.request.ProductUnitRequest;
import com.g4fpt.sms.product.dto.response.ProductUnitResponse;
import com.g4fpt.sms.product.entity.Product;
import com.g4fpt.sms.product.entity.ProductUnit;
import java.util.List;

public interface ProductUnitService {
    ProductUnit create(ProductUnitRequest productUnitRequest, Product product);
    ProductUnit update(ProductUnitRequest productUnitRequest, Product product);
    void deleteById(Long unitId, Long productId);
    List<ProductUnitResponse> findAll();
    ProductUnitResponse findById(Long id);
    void validate(ProductUnitRequest productUnitRequest, Long excludeId);
    List<ProductUnit> productUnitSync(List<ProductUnitRequest> productUnitRequests, Product product);
}
