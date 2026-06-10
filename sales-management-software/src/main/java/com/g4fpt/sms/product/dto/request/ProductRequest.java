package com.g4fpt.sms.product.dto.request;

import com.g4fpt.sms.product.entity.Brand;
import com.g4fpt.sms.product.entity.Category;
import com.g4fpt.sms.product.enums.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "Image in required")
    private String imageUrl;
    private String description;
    @NotNull(message = "Status is required")
    private ProductStatus status;
    private String note;
    @NotEmpty(message = "Need at least 1 unit")
    private List<ProductUnitRequest> productUnitsRequest;
}
