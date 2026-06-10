package com.g4fpt.sms.product.service.impl;

import com.g4fpt.sms.product.dto.request.ProductRequest;
import com.g4fpt.sms.product.dto.request.ProductUnitRequest;
import com.g4fpt.sms.product.dto.response.ProductResponse;
import com.g4fpt.sms.product.dto.response.ProductUnitResponse;
import com.g4fpt.sms.product.entity.Product;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.entity.Unit;
import com.g4fpt.sms.product.exception.NotFoundException;
import com.g4fpt.sms.product.exception.ValidationException;
import com.g4fpt.sms.product.mapper.ProductMapper;
import com.g4fpt.sms.product.repository.*;
import com.g4fpt.sms.product.service.ProductService;
import com.g4fpt.sms.product.util.ValidationError;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final UnitRepository unitRepository;
    private final ProductUnitRepository productUnitRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              BrandRepository brandRepository,
                              CategoryRepository categoryRepository,
                              ProductMapper productMapper,
                              UnitRepository unitRepository,
                              ProductUnitRepository productUnitRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
        this.unitRepository = unitRepository;
        this.productUnitRepository = productUnitRepository;
    }

    @Override
    public void create(ProductRequest productRequest) {
        validate(productRequest, null);
        Product product = new Product();
        requestToProduct(productRequest, product);
        productRepository.save(product);
    }

    @Override
    public void update(long id, ProductRequest productRequest) {
        validate(productRequest, id);
        Product product = getProductById(id);
        requestToProduct(productRequest, product);
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

        // Check từng productUnit trong list
        for (ProductUnitRequest unitRequest : productRequest.getProductUnitsRequest()) {
            if (productUnitRepository.existsBySkuIgnoreCase(unitRequest.getSku())) {
                errors.add(new ValidationError("sku", "SKU " + unitRequest.getSku() + " đã tồn tại"));
            }
            if (productUnitRepository.existsByBarcodeUnitIgnoreCase(unitRequest.getBarcodeUnit())) {
                errors.add(new ValidationError("barcodeUnit", "Barcode " + unitRequest.getBarcodeUnit() + " đã tồn tại"));
            }
        }

        // Check phải có đúng 1 base unit
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
        for(ProductUnitRequest productUnitRequest : productRequest.getProductUnitsRequest()){
            ProductUnit productUnit = new ProductUnit();

            Unit unit = unitRepository.findById(productUnitRequest.getUnitId())
                    .orElseThrow(() -> new NotFoundException("Unit not found"));

            productUnit.setUnit(unit);
            productUnit.setProduct(product);

            productUnit.setConventionValue(productUnitRequest.getConventionValue());
            productUnit.setPrice(productUnitRequest.getPrice());
            productUnit.setBarcodeUnit(productUnitRequest.getBarcodeUnit());
            productUnit.setIsBaseUnit(
                    Boolean.TRUE.equals(productUnitRequest.getIsBaseUnit()));
            productUnit.setSku(productUnitRequest.getSku());

            product.getProductUnits().add(productUnit);
        }

        product.setCategory(categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found")));
        product.setBrand(brandRepository.findById(productRequest.getBrandId())
                .orElseThrow(() -> new NotFoundException("Brand not found")));
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setStatus(productRequest.getStatus());
        product.setNote(productRequest.getNote());
    }
}
