package com.g4fpt.sms.product.dto.response;

import com.g4fpt.sms.product.enums.BrandStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandResponse {
    Long id;
    String name;
    BrandStatus status;
}
