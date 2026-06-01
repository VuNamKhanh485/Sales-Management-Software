package com.g4fpt.sms.product.service.impl;

import com.g4fpt.sms.product.dto.CategoryRequest;
import com.g4fpt.sms.product.entity.Category;
import com.g4fpt.sms.product.repository.CategoryRepository;
import com.g4fpt.sms.product.service.CategoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    @Override
    public Category save(CategoryRequest categoryRequest) {
        Category category = new Category();

        category.setName(categoryRequest.getCategoryName());
        category.setDescription(categoryRequest.getDescription());
        category.setStatus(categoryRequest.getCategoryStatus());
        category.setCreatedDate(LocalDateTime.now());

        return  categoryRepository.save(category);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public Category update(long id, CategoryRequest categoryRequest) {
        Category category = findById(id);

        if (category != null) {
            category.setName(categoryRequest.getCategoryName());
            category.setDescription(categoryRequest.getDescription());
            category.setStatus(categoryRequest.getCategoryStatus());

            category.setUpdateDate(LocalDateTime.now());

            return categoryRepository.save(category);
        }
        return null;
    }
}
