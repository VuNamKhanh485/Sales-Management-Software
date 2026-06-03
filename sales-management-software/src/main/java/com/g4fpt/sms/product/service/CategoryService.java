package com.g4fpt.sms.product.service;

import com.g4fpt.sms.product.dto.CategoryRequest;
import com.g4fpt.sms.product.entity.Category;

import java.util.List;

public interface CategoryService {
    Category save(CategoryRequest categoryRequest);
    List<Category> findAll();
    Category findById(long id);
    void deleteById(long id);
    Category update(long id, CategoryRequest categoryRequest);
}
