package com.g4fpt.sms.product.service;

import com.g4fpt.sms.product.dto.request.BrandRequest;
import com.g4fpt.sms.product.dto.request.ProductFilterRequest;
import com.g4fpt.sms.product.dto.request.ProductRequest;
import com.g4fpt.sms.product.dto.response.ProductResponse;
import com.g4fpt.sms.product.util.ValidationError;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    void create(ProductRequest productRequest, MultipartFile file) throws IOException;
    void update(long id, ProductRequest productRequest, MultipartFile file) throws IOException;
    void deleteById(long id);
    ProductResponse findById(long id);
    List<ProductResponse> findByName(String name);
    List<ProductResponse> findByBrand(Long brandId);
    List<ProductResponse> findByCategory(Long categoryId);
    void validate(ProductRequest productRequest, Long id);

    Page<ProductResponse> findAll(ProductFilterRequest filter, int page, int size,
                                  String sortField, String sortDir);
}
