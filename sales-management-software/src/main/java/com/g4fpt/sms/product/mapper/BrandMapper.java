package com.g4fpt.sms.product.mapper;

import com.g4fpt.sms.product.dto.request.BrandRequest;
import com.g4fpt.sms.product.dto.response.BrandResponse;
import com.g4fpt.sms.product.entity.Brand;
import org.springframework.stereotype.Component;

@Component
public class BrandMapper {

    public Brand toEntity(Brand brand, BrandRequest request) {
        if (request == null) {
            return null;
        }

        brand.setName(request.getBrandName().trim());
        brand.setStatus(request.getBrandStatus());

        return brand;
    }

    public BrandResponse toResponse(Brand brand) {
        if (brand == null) {
            return null;
        }

        BrandResponse response = new BrandResponse();

        response.setId(brand.getId());
        response.setName(brand.getName());
        response.setStatus(brand.getStatus());

        return response;
    }
}
