package com.g4fpt.sms.product.service;

import com.g4fpt.sms.product.dto.request.CategoryRequest;
import com.g4fpt.sms.product.dto.response.CategoryResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {
    CategoryResponse create(CategoryRequest categoryRequest);
    void deleteById(long id);
    void update(long id, CategoryRequest categoryRequest);
    Page<CategoryResponse> findAll(String keyword, int page, int size, String sortField, String sortDirection);
    CategoryResponse findById(long id);
    List<CategoryResponse> findAll();
}