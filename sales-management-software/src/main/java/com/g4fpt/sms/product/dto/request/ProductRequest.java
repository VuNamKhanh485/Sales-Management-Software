package com.g4fpt.sms.product.dto.request;

import com.g4fpt.sms.product.entity.Brand;
import com.g4fpt.sms.product.entity.Category;
import com.g4fpt.sms.product.enums.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductRequest {
    @NotBlank(message = "Category is required")
    private Long categoryId;
    @NotBlank(message = "Brand is required")
    private Long brandId;
    @NotBlank(message = "Name is required")
    @Size(message = "Name must be at least 3 chars")
    private String name;
    private String imageUrl;
    private String description;
    private ProductStatus status;
    private String note;
    private List<ProductUnitRequest> productUnitsRequest;
}
