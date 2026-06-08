package com.g4fpt.sms.product.dto.response;

import com.g4fpt.sms.product.enums.CategoryStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryResponse {
    Long id;
    String name;
    String description;
    CategoryStatus categoryStatus;
}
