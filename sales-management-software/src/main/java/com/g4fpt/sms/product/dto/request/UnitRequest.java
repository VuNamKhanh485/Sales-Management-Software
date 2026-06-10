package com.g4fpt.sms.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnitRequest {
    @NotBlank(message = "Name is required")
    private String name;
}
