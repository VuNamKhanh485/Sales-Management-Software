package com.g4fpt.sms.product.service.impl;

import com.g4fpt.sms.product.dto.request.BrandRequest;
import com.g4fpt.sms.product.dto.request.ProductUnitRequest;
import com.g4fpt.sms.product.dto.response.ProductUnitResponse;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.exception.NotFoundException;
import com.g4fpt.sms.product.mapper.ProductUnitMapper;
import com.g4fpt.sms.product.repository.ProductRepository;
import com.g4fpt.sms.product.repository.ProductUnitRepository;
import com.g4fpt.sms.product.repository.UnitRepository;
import com.g4fpt.sms.product.service.ProductUnitService;
import com.g4fpt.sms.product.util.ValidationError;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductUnitServiceImpl implements ProductUnitService {

    private final ProductUnitRepository productUnitRepository;
    private final ProductRepository productRepository;
    private final UnitRepository unitRepository;
    private final ProductUnitMapper productUnitMapper;

    public ProductUnitServiceImpl(ProductUnitRepository productUnitRepository, ProductRepository productRepository, UnitRepository unitRepository, ProductUnitMapper productUnitMapper) {
        this.productUnitRepository = productUnitRepository;
        this.productRepository = productRepository;
        this.unitRepository = unitRepository;
        this.productUnitMapper = productUnitMapper;
    }

    @Override
    public List<ProductUnitResponse> findAll() {
        return productUnitRepository.findAll()
                .stream()
                .map(productUnitMapper::toResponse)
                .toList();
    }

    @Override
    public void create(ProductUnitRequest productUnitRequest) {
        validate(productUnitRequest);
        ProductUnit productUnit = new ProductUnit();
        requestToEntity(productUnitRequest, productUnit);
        productUnitRepository.save(productUnit);
    }

    @Override
    public void update(Long id, ProductUnitRequest productUnitRequest) {
        validate(productUnitRequest);
        ProductUnit productUnit = getProductUnitById(id);
        requestToEntity(productUnitRequest, productUnit);
        productUnitRepository.save(productUnit);
    }

    @Override
    public void deleteById(Long id) {
        //cần có phần orderTranscation
    }

    @Override
    public ProductUnitResponse findById(Long id) {
        return  productUnitMapper.toResponse(getProductUnitById(id));
    }

    @Override
    public List<ValidationError> validate(ProductUnitRequest productUnitRequest) {
        List<ValidationError> errors = new ArrayList<>();
        if(productUnitRepository.existsByBarcodeUnitIgnoreCase(productUnitRequest.getBarcodeUnit())) {
            errors.add(new ValidationError("Barcode","Barcode is existed"));
        }
        if (productUnitRepository.existsBySkuIgnoreCase(productUnitRequest.getSku())) {
            errors.add(new ValidationError("Sku","Sku is existed"));
        }

        return errors;
    }

    private void requestToEntity(ProductUnitRequest productUnitRequest, ProductUnit productUnit) {
        productUnit.setProduct(
                productRepository.findById(
                                productUnitRequest.getProductId())
                        .orElseThrow(() -> new NotFoundException("product not found"))
        );
        productUnit.setUnit(
                unitRepository.findById(
                                productUnitRequest.getUnitId())
                        .orElseThrow(() -> new NotFoundException("unit not found"))
        );

        productUnit.setSku(productUnitRequest.getSku());
        productUnit.setBarcodeUnit(productUnitRequest.getBarcodeUnit());
        productUnit.setConventionValue(productUnitRequest.getConventionValue());
        productUnit.setPrice(productUnitRequest.getPrice());
        productUnit.setIsBaseUnit(
                productUnitRequest.getIsBaseUnit() != null ? productUnitRequest.getIsBaseUnit() : false
        );
    }

    private ProductUnit getProductUnitById(Long id) {
        return productUnitRepository.findById(id).orElseThrow(() -> new NotFoundException("product not found"));
    }
}