package com.g4fpt.sms.product.service.impl;

import com.g4fpt.sms.common.exception.ResourceInUseException;
import com.g4fpt.sms.product.dto.request.CategoryRequest;
import com.g4fpt.sms.product.dto.response.CategoryResponse;
import com.g4fpt.sms.product.entity.Category;
import com.g4fpt.sms.common.exception.DuplicateException;
import com.g4fpt.sms.common.exception.NotFoundException;
import com.g4fpt.sms.product.enums.CategoryStatus;
import com.g4fpt.sms.product.mapper.CategoryMapper;
import com.g4fpt.sms.product.repository.CategoryRepository;
import com.g4fpt.sms.product.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
    public CategoryResponse create(CategoryRequest categoryRequest) {
        if(categoryRepository.existsByNameIgnoreCase(categoryRequest.getCategoryName())){
            throw new DuplicateException("This name is already in use");
        }
        Category category = categoryMapper.toEntity(categoryRequest);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    public Page<CategoryResponse> findAll(String keyword, int page, int size, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Category> categoryPage;
        if(keyword == null || keyword.isBlank()){
            categoryPage = categoryRepository.findAll(pageable);
        }else{
            categoryPage = categoryRepository.findByNameContainingIgnoreCase(keyword, pageable);
        }

        return categoryPage.map(categoryMapper::toResponse);
    }

    @Override
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    public List<CategoryResponse> findAllActive() {
        return categoryRepository.findByStatus(CategoryStatus.ACTIVE)
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse findById(long id) {
        return categoryMapper.toResponse(getCategoryById(id));
    }

    @Override
    public void deleteById(long id) {
        Category category = getCategoryById(id);

        if(categoryRepository.existInOrderTransaction(id)){
            throw new ResourceInUseException("Danh sách đã tồn tại trong giao dịch");
        }
        categoryRepository.delete(category);
    }

    @Override
    public void update(long id, CategoryRequest categoryRequest) {
        Category category = getCategoryById(id);
        if(categoryRepository.existsByNameIgnoreCaseAndIdNot(categoryRequest.getCategoryName(), id)){
            throw new DuplicateException("This name is already in use");
        }
        category.setName(categoryRequest.getCategoryName());
        category.setDescription(categoryRequest.getDescription());
        category.setStatus(categoryRequest.getCategoryStatus());

        categoryRepository.save(category);
    }

    private Category getCategoryById(Long id){
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
    }
}
