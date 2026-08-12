package com.g4fpt.sms.product.dto.request;


import com.g4fpt.sms.product.enums.ProductStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ProductRequest {
    @NotNull(message = "Category is required")
    private Long categoryId;
    @NotNull(message = "Brand is required")
    private Long brandId;
    @NotBlank(message = "Name is required")
    @Size(message = "Name must be at least 3 chars")
    private String name;
    private MultipartFile imageFile;
    private String imageName;
    @Size(max = 255, message = "Mô tả không được vượt quá 255 ký tự")
    private String description;
    @NotNull(message = "Status is required")
    private ProductStatus status;
    private String note;
    private List<Long> supplierIds;
    @NotEmpty(message = "Need at least 1 unit")
    @Valid
    private List<ProductUnitRequest> productUnitsRequest;
}
