package com.g4fpt.sms.product.mapper;


import com.g4fpt.sms.product.dto.request.ProductUnitRequest;
import com.g4fpt.sms.product.dto.response.ProductUnitResponse;
import com.g4fpt.sms.product.entity.ProductUnit;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ProductUnitMapper {
    private final UnitMapper unitMapper;

    public ProductUnitRequest toRequest(ProductUnitResponse response) {

        if (response == null) {
            return null;
        }

        ProductUnitRequest request = new ProductUnitRequest();

        request.setId(response.getId());
        request.setUnitId(response.getUnit().getId());
        request.setSku(response.getSku());
        request.setPrice(response.getPrice());
        request.setIsBaseUnit(response.getIsBaseUnit());
        request.setConventionValue(response.getConventionValue());
        request.setBarcodeUnit(response.getBarcodeUnit());

        return request;
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
