package com.g4fpt.sms.product.mapper;


import com.g4fpt.sms.product.dto.response.ProductUnitResponse;
import com.g4fpt.sms.product.entity.ProductUnit;
import org.springframework.stereotype.Component;

@Component
public class ProductUnitMapper {
    private final UnitMapper unitMapper;

    public ProductUnitMapper(UnitMapper unitMapper) {
        this.unitMapper = unitMapper;
    }


    public ProductUnitResponse toResponse(ProductUnit productUnit) {
        if (productUnit == null) {
            return null;
        }

        ProductUnitResponse response = new ProductUnitResponse();

        response.setId(productUnit.getId());
        response.setUnit(unitMapper.toResponse(productUnit.getUnit()));
        response.setBarcodeUnit(productUnit.getBarcodeUnit());
        response.setIsBaseUnit(productUnit.getIsBaseUnit());
        response.setSku(productUnit.getSku());
        response.setConventionValue(productUnit.getConventionValue());
        response.setPrice(productUnit.getPrice());

        return response;
    }
}
