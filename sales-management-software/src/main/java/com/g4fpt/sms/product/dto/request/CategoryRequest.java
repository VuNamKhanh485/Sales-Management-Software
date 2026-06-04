package com.g4fpt.sms.product.dto.request;

import com.g4fpt.sms.product.enums.CategoryStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {
    @NotBlank
    private String categoryName;

    private String description;

    private CategoryStatus categoryStatus;
}
