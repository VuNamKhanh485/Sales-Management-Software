package com.g4fpt.sms.product.dto.request;

import com.g4fpt.sms.product.enums.UnitStatus;
import com.g4fpt.sms.product.service.impl.UnitServiceImpl;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnitRequest {
    @NotBlank(message = "Name is required")
    private String name;
    @NotNull(message = "Status is required")
    private UnitStatus status;
}
