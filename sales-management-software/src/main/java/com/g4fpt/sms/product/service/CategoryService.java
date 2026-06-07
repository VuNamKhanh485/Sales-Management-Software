package com.g4fpt.sms.product.service;

import com.g4fpt.sms.product.dto.request.CategoryRequest;
import com.g4fpt.sms.product.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {
    void create(CategoryRequest categoryRequest);
    void deleteById(long id);
    void update(long id, CategoryRequest categoryRequest);
    List<CategoryResponse> findAll();
    CategoryResponse findById(long id);

}
