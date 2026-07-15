package com.g4fpt.sms.product.dto.response;


import com.g4fpt.sms.product.enums.ProductStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductResponse {
    private Long id;
    private CategoryResponse category;
    private BrandResponse brand;
    private String name;
    private String imageName;
    private String description;
    private ProductStatus status;
    private String note;
    private List<ProductUnitResponse> productUnitsResponses;

    public String getBaseSku(){
        if (productUnitsResponses == null) {
            return "-";
        }

        return productUnitsResponses.stream()
                .filter(u -> Boolean.TRUE.equals(u.getIsBaseUnit()))
                .findFirst()
                .map(ProductUnitResponse::getSku)
                .orElse("-");
    }
}
