package com.g4fpt.sms.product.service.impl;

import com.g4fpt.sms.common.exception.ResourceInUseException;
import com.g4fpt.sms.product.dto.request.ProductUnitRequest;
import com.g4fpt.sms.product.dto.response.ProductUnitResponse;
import com.g4fpt.sms.product.entity.Product;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.common.exception.NotFoundException;
import com.g4fpt.sms.common.exception.ValidationException;
import com.g4fpt.sms.product.mapper.ProductUnitMapper;
import com.g4fpt.sms.product.repository.ProductRepository;
import com.g4fpt.sms.product.repository.ProductUnitRepository;
import com.g4fpt.sms.product.repository.UnitRepository;
import com.g4fpt.sms.product.service.ProductUnitService;
import com.g4fpt.sms.product.util.ValidationError;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductUnitServiceImpl implements ProductUnitService {

    private final ProductUnitRepository productUnitRepository;
    private final ProductRepository productRepository;
    private final UnitRepository unitRepository;
    private final ProductUnitMapper productUnitMapper;

    @Override
    public List<ProductUnitResponse> findAll() {
        return productUnitRepository.findAll()
                .stream()
                .map(productUnitMapper::toResponse)
                .toList();
    }

    @Override
    public ProductUnit create(ProductUnitRequest productUnitRequest, Product product) {
            validate(productUnitRequest, null);
            ProductUnit productUnit = new ProductUnit();
            requestToEntity(productUnitRequest, productUnit, product);
        return productUnit;
    }

    @Override
    public ProductUnit update(ProductUnitRequest productUnitRequest, Product product) {
        validate(productUnitRequest, productUnitRequest.getId());
        ProductUnit productUnit = getProductUnitByIdAndProduct(productUnitRequest.getId(), product.getId());
        requestToEntity(productUnitRequest, productUnit, product);
        return productUnit;
    }

    @Override
    public void deleteById(Long unitId, Long productId) {
        ProductUnit productUnit = getProductUnitByIdAndProduct(unitId, productId);

        if(productUnitRepository.existInOrderTransaction(unitId)){
            throw new ResourceInUseException("Sản phẩm này đã nằm trong giao dịch");
        }

        productUnitRepository.delete(productUnit);
    }

    public List<ProductUnit> productUnitSync(List<ProductUnitRequest> productUnitRequests, Product product) {
        List<ProductUnit> productUnitList = new ArrayList<>();

        Set<Long> requestIds = productUnitRequests.stream()
                .map(ProductUnitRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for(ProductUnit unit : product.getProductUnits()){
            if(!requestIds.contains(unit.getId())){
                deleteById(unit.getId(), product.getId());
            }
        }

        for(int i = 0; i < product.getProductUnits().size(); i++){
            ProductUnitRequest productUnitRequest = productUnitRequests.get(i);
            try{
            if(productUnitRequest.getId() == null){
                productUnitList.add(create(productUnitRequest, product));
            }else{
                productUnitList.add(update(productUnitRequest, product));
            }
        }catch(ValidationException e){
                for (ValidationError error : e.getErrors()) {
                    error.setField(
                            "productUnitsRequest[" + i + "]." + error.getField()
                    );
                }

                throw e;
            }
        }
        return productUnitList;
    }

    @Override
    public ProductUnitResponse findById(Long id) {
        return  productUnitMapper.toResponse(getProductUnitById(id));
    }

    @Override
    public List<ProductUnitResponse> findByProductId(Long id) {
        return productUnitRepository.findByProduct_Id(id)
                .stream()
                .map(productUnitMapper::toResponse)
                .toList();
    }

    @Override
    public void validate(ProductUnitRequest productUnitRequest, Long excludeId) {
        List<ValidationError> errors = new ArrayList<>();
        if(excludeId != null) {
            if(productUnitRepository.existsByBarcodeUnitIgnoreCaseAndIdNot(productUnitRequest.getBarcodeUnit(), excludeId)) {
                errors.add(new ValidationError("barcodeUnit","Barcode is existed"));
            }
            if (productUnitRepository.existsBySkuIgnoreCaseAndIdNot(productUnitRequest.getSku(), excludeId)) {
                errors.add(new ValidationError("sku","Sku is existed"));
            }
        }else {
            if (productUnitRepository.existsByBarcodeUnitIgnoreCase(productUnitRequest.getBarcodeUnit())) {
                errors.add(new ValidationError("barcodeUnit", "Barcode is existed"));
            }
            if (productUnitRepository.existsBySkuIgnoreCase(productUnitRequest.getSku())) {
                errors.add(new ValidationError("sku", "Sku is existed"));
            }
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private void requestToEntity(ProductUnitRequest productUnitRequest, ProductUnit productUnit, Product product) {
        productUnit.setProduct(product);
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
                Boolean.TRUE.equals(productUnitRequest.getIsBaseUnit()));
    }

    private ProductUnit getProductUnitById(Long id){
        return productUnitRepository.findById(id).orElseThrow(() -> new NotFoundException("Product unit not found"));
    }

    private ProductUnit getProductUnitByIdAndProduct(Long id, Long productId) {
        return productUnitRepository.findByIdAndProduct_Id(id, productId);
    }
}