package com.g4fpt.sms.product.service.impl;

import com.g4fpt.sms.product.dto.request.CategoryRequest;
import com.g4fpt.sms.product.dto.response.CategoryResponse;
import com.g4fpt.sms.product.entity.Category;
import com.g4fpt.sms.product.mapper.CategoryMapper;
import com.g4fpt.sms.product.repository.CategoryRepository;
import com.g4fpt.sms.product.service.CategoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }


    @Override
    public void create(CategoryRequest categoryRequest) {
        Category category = categoryMapper.toEntity(categoryRequest);
        categoryRepository.save(category);
    }

    @Override
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse findById(long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        return categoryMapper.toResponse(category);
    }

    @Override
    public void deleteById(long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public void update(long id, CategoryRequest categoryRequest) {
        Category category = getCategoryById(id);

        category.setName(categoryRequest.getCategoryName());
        category.setDescription(categoryRequest.getDescription());
        category.setStatus(categoryRequest.getCategoryStatus());
        category.setUpdateAt(LocalDateTime.now());
        categoryRepository.save(category);
    }

    private Category getCategoryById(Long id){
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
    }
}
