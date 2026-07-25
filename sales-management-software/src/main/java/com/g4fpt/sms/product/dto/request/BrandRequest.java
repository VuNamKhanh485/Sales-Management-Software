package com.g4fpt.sms.product.dto.request;

import com.g4fpt.sms.product.enums.BrandStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BrandRequest {
    @NotBlank(message = "Name is required")
    private String brandName;
    @NotNull(message = "Status is required")
    private BrandStatus brandStatus;
}
