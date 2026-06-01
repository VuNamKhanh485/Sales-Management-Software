package com.g4fpt.sms.product.dto;

import com.g4fpt.sms.product.entity.Brand;
import com.g4fpt.sms.product.entity.Category;
import com.g4fpt.sms.product.entity.ProductUnit;
import com.g4fpt.sms.product.enums.ProductStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductRequest {
    private Category category;
    private Brand brand;
    private String name;
    private String description;
    private ProductStatus status;
    private String note;
    private List<ProductUnitRequest> productUnitsRequest;
}
