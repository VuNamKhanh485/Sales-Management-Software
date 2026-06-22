package com.g4fpt.sms.product.service.impl;

import com.g4fpt.sms.product.dto.request.ProductFilterRequest;
import com.g4fpt.sms.product.dto.request.ProductRequest;
import com.g4fpt.sms.product.dto.response.ProductResponse;
import com.g4fpt.sms.product.entity.Product;
import com.g4fpt.sms.common.exception.NotFoundException;
import com.g4fpt.sms.common.exception.ValidationException;
import com.g4fpt.sms.product.mapper.ProductMapper;
import com.g4fpt.sms.product.repository.*;
import com.g4fpt.sms.product.service.ProductService;
import com.g4fpt.sms.product.service.ProductUnitService;
import com.g4fpt.sms.product.util.ProductSpecification;
import com.g4fpt.sms.product.util.ValidationError;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final ProductUnitService productUnitService;

    @Override
    public void create(ProductRequest productRequest) {
        validate(productRequest, null);
        Product product = new Product();
        requestToProduct(productRequest, product);
        product.setProductUnits(
                productUnitService.productUnitSync(productRequest.getProductUnitsRequest(),
                        product));
        productRepository.save(product);
    }

    @Override
    public void update(long id, ProductRequest productRequest) {
        validate(productRequest, id);
        Product product = getProductById(id);
        requestToProduct(productRequest, product);
        product.setProductUnits(
                productUnitService.productUnitSync(productRequest.getProductUnitsRequest(),
                        product));
        productRepository.save(product);
    }

    @Override
    public ProductResponse findById(long id) {
        return productMapper.toResponse(getProductById(id));
    }

    @Override
    public List<ProductResponse> findByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> findByBrand(Long brandId) {
        return productRepository.findByBrand_Id(brandId)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> findByCategory(Long categoryId) {
        return productRepository.findByCategory_Id(categoryId)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteById(long id) {
        getProductById(id); // kiểm tra tồn tại
        // TODO: kiểm tra ràng buộc orderTransaction trước khi xóa
        productRepository.deleteById(id);
    }

    @Override
    public Page<ProductResponse> findAll(ProductFilterRequest filter, int size, int page,
                                         String sortField, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<Product> spec = ProductSpecification.fromFilter(filter);

        return productRepository.findAll(spec, pageable)
                .map(productMapper::toResponse);
    }

    private Product getProductById(long id) {
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException("product not found"));
    }

    @Override
    public void validate(ProductRequest productRequest, Long excludeId) {
        List<ValidationError> errors = new ArrayList<>();

        // Check tên sản phẩm trùng
        if(excludeId == null){
            if (productRepository.existsByNameIgnoreCase(productRequest.getName())) {
                errors.add(new ValidationError("name", "Tên sản phẩm đã tồn tại"));
            }
        }else{
            if (productRepository.existsByNameIgnoreCaseAndIdNot(productRequest.getName(), excludeId)) {
                errors.add(new ValidationError("name", "Tên sản phẩm đã tồn tại"));
            }
        }

        long baseUnitCount = productRequest.getProductUnitsRequest()
                .stream()
                .filter(u -> Boolean.TRUE.equals(u.getIsBaseUnit()))
                .count();
        if (baseUnitCount == 0) {
            errors.add(new ValidationError("productUnits", "Phải có ít nhất 1 base unit"));
        }
        if (baseUnitCount > 1) {
            errors.add(new ValidationError("productUnits", "Chỉ được có 1 base unit"));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private void requestToProduct(ProductRequest productRequest, Product product) {
        product.setCategory(categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found")));
        product.setBrand(brandRepository.findById(productRequest.getBrandId())
                .orElseThrow(() -> new NotFoundException("Brand not found")));
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setImageUrl(productRequest.getImageUrl());
        product.setStatus(productRequest.getStatus());
        product.setNote(productRequest.getNote());
    }
}
