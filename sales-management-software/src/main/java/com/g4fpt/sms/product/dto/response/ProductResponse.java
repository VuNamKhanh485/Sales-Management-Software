package com.g4fpt.sms.product.dto.response;

import com.g4fpt.sms.product.dto.request.ProductUnitRequest;
import com.g4fpt.sms.product.entity.Brand;
import com.g4fpt.sms.product.entity.Category;
import com.g4fpt.sms.product.enums.ProductStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductResponse {
    private Long id;
    private Category category;
    private Brand brand;
    private String name;
    private String imageUrl;
    private String description;
    private ProductStatus status;
    private String note;
    private List<ProductUnitRequest> productUnitsRequest;
}
