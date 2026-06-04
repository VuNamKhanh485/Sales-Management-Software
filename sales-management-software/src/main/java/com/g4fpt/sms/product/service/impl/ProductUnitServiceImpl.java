package com.g4fpt.sms.product.service.impl;

import com.g4fpt.sms.product.dto.request.ProductUnitRequest;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.repository.ProductRepository;
import com.g4fpt.sms.product.repository.ProductUnitRepository;
import com.g4fpt.sms.product.repository.UnitRepository;
import com.g4fpt.sms.product.service.ProductUnitService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductUnitServiceImpl implements ProductUnitService {

    private final ProductUnitRepository productUnitRepository;
    private final ProductRepository productRepository;
    private final UnitRepository unitRepository;

    public ProductUnitServiceImpl(ProductUnitRepository productUnitRepository, ProductRepository productRepository, UnitRepository unitRepository) {
        this.productUnitRepository = productUnitRepository;
        this.productRepository = productRepository;
        this.unitRepository = unitRepository;
    }

    @Override
    public List<ProductUnit> findAll() {
        return productUnitRepository.findAll();
    }

    @Override
    public ProductUnit create(ProductUnitRequest productUnitRequest) {
        ProductUnit productUnit = new ProductUnit();

        requestToEntity(productUnitRequest, productUnit);

        productUnit.setCreatedAt(LocalDateTime.now());
        return productUnitRepository.save(productUnit);
    }

    @Override
    public ProductUnit update(Long id, ProductUnitRequest productUnitRequest) {
        ProductUnit productUnit = findById(id);


            requestToEntity(productUnitRequest, productUnit);

            productUnit.setUpdatedAt(LocalDateTime.now());
            return productUnitRepository.save(productUnit);
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public ProductUnit findById(Long id) {
        return productUnitRepository.findById(id).orElseThrow(() -> new RuntimeException("product unit not found"));
    }

    private void requestToEntity(ProductUnitRequest productUnitRequest, ProductUnit productUnit) {
        productUnit.setProduct(
                productRepository.findById(
                                productUnitRequest.getProductId())
                        .orElseThrow(() -> new RuntimeException("product not found"))
        );
        productUnit.setUnit(
                unitRepository.findById(
                                productUnitRequest.getUnitId())
                        .orElseThrow(() -> new RuntimeException("unit not found"))
        );

        productUnit.setSku(productUnitRequest.getSku());
        productUnit.setBarcodeUnit(productUnitRequest.getBarcodeUnit());
        productUnit.setConventionValue(productUnitRequest.getConventionValue());
        productUnit.setPrice(productUnitRequest.getUnitPrice());
        productUnit.setIsBaseUnit(productUnitRequest.getIsBaseUnit());
    }
}