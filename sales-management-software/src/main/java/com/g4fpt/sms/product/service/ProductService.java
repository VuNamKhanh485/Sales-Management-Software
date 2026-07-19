package com.g4fpt.sms.product.service;

import com.g4fpt.sms.product.dto.request.ProductFilterRequest;
import com.g4fpt.sms.product.dto.request.ProductRequest;
import com.g4fpt.sms.product.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

import java.io.IOException;

public interface ProductService {
    void create(ProductRequest productRequest) throws IOException;
    void update(long id, ProductRequest productRequest) throws IOException;
    void deleteById(long id);
    ProductResponse findById(long id);
    void validate(ProductRequest productRequest, Long id);

    Page<ProductResponse> findAll(ProductFilterRequest filter, int page, int size,
                                  String sortField, String sortDir);
}
