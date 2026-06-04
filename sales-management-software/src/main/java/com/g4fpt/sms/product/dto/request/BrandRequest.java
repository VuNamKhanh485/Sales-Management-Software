package com.g4fpt.sms.product.dto.request;

import com.g4fpt.sms.product.enums.BrandStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BrandRequest {
    private String brandName;
    private BrandStatus brandStatus;
}
