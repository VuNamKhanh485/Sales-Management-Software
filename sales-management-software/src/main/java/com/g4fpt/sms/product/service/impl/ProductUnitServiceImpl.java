package com.g4fpt.sms.product.service.impl;

import com.g4fpt.sms.product.dto.request.ProductUnitRequest;
import com.g4fpt.sms.product.dto.response.ProductUnitResponse;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.mapper.ProductUnitMapper;
import com.g4fpt.sms.product.repository.ProductRepository;
import com.g4fpt.sms.product.repository.ProductUnitRepository;
import com.g4fpt.sms.product.repository.UnitRepository;
import com.g4fpt.sms.product.service.ProductUnitService;
import org.springframework.stereotype.Service;

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
        ProductUnit productUnit = new ProductUnit();
        requestToEntity(productUnitRequest, productUnit);
        productUnitRepository.save(productUnit);
    }

    @Override
    public void update(Long id, ProductUnitRequest productUnitRequest) {
        ProductUnit productUnit = getProducUnitById(id);
       requestToEntity(productUnitRequest, productUnit);
        productUnitRepository.save(productUnit);
    }

    @Override
    public void deleteById(Long id) {
        //cần có phần orderTranscation
    }

    @Override
    public ProductUnitResponse findById(Long id) {
        ProductUnit productUnit = productUnitRepository.findById(id).orElseThrow(() -> new RuntimeException("product unit not found"));
        return  productUnitMapper.toResponse(productUnit);
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
        productUnit.setPrice(productUnitRequest.getPrice());
        productUnit.setIsBaseUnit(
                productUnitRequest.getIsBaseUnit() != null ? productUnitRequest.getIsBaseUnit() : false
        );
    }

    private ProductUnit getProducUnitById(Long id) {
        return productUnitRepository.findById(id).orElseThrow(() -> new RuntimeException("product not found"));
    }
}