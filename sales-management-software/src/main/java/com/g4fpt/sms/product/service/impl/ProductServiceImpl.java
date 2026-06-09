package com.g4fpt.sms.product.service.impl;

import com.g4fpt.sms.product.dto.request.ProductRequest;
import com.g4fpt.sms.product.dto.request.ProductUnitRequest;
import com.g4fpt.sms.product.dto.response.ProductResponse;
import com.g4fpt.sms.product.dto.response.ProductUnitResponse;
import com.g4fpt.sms.product.entity.Product;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.entity.Unit;
import com.g4fpt.sms.product.mapper.ProductMapper;
import com.g4fpt.sms.product.repository.BrandRepository;
import com.g4fpt.sms.product.repository.CategoryRepository;
import com.g4fpt.sms.product.repository.ProductRepository;
import com.g4fpt.sms.product.repository.UnitRepository;
import com.g4fpt.sms.product.service.ProductService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final UnitRepository unitRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              BrandRepository brandRepository,
                              CategoryRepository categoryRepository,
                              ProductMapper productMapper,
                              UnitRepository unitRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
        this.unitRepository = unitRepository;
    }

    @Override
    public void create(ProductRequest productRequest) {
        Product product = new Product();

        for(ProductUnitRequest productUnitRequest : productRequest.getProductUnitsRequest()){
            ProductUnit productUnit = new ProductUnit();

            Unit unit = unitRepository.findById(productUnitRequest.getUnitId())
                    .orElseThrow(() -> new RuntimeException("Unit not found"));
            productUnit.setUnit(unit);
            productUnit.setProduct(product);
            productUnit.setConventionValue(productUnitRequest.getConventionValue());
            productUnit.setPrice(productUnitRequest.getPrice());
            productUnit.setBarcodeUnit(productUnitRequest.getBarcodeUnit());
            productUnit.setIsBaseUnit(
                    productUnitRequest.getIsBaseUnit() != null ? productUnitRequest.getIsBaseUnit() : false
            );
            productUnit.setSku(productUnitRequest.getSku());

            product.getProductUnits().add(productUnit);
        }

        requestToProduct(productRequest, product);

        product.setCreatedAt(LocalDateTime.now());

        productRepository.save(product);
    }

    @Override
    public void update(long id, ProductRequest productRequest) {
        Product product = getProductById(id);
        requestToProduct(productRequest, product);
        productRepository.save(product);

    }

    @Override
    public ProductResponse findById(long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("product not found"));
        return productMapper.toResponse(product);
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
        //cần có phần orderTranscation
    }

    @Override
    public List<ProductResponse> findAll() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    private Product getProductById(long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("product not found"));
    }

    private void requestToProduct(ProductRequest productRequest, Product product) {
        product.setCategory(categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found")));
        product.setBrand(brandRepository.findById(productRequest.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found")));
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setStatus(productRequest.getStatus());
        product.setNote(productRequest.getNote());
    }
}
