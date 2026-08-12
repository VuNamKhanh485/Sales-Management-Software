package com.g4fpt.sms.product.dto.request;

import com.g4fpt.sms.product.enums.CategoryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {
    @NotBlank(message = "Name is required")
    private String categoryName;
    @Size(max = 255, message = "Mô tả không được vượt quá 255 ký tự")
    private String description;
    @NotNull(message = "Status is required")
    private CategoryStatus categoryStatus;
}
