package com.g4fpt.sms.product.dto.request;

import com.g4fpt.sms.product.enums.ProductStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductFilterRequest {
    private String keyword; // tìm theo tên
    private Long brandId;
    private Long categoryId;
    private ProductStatus status;
}
