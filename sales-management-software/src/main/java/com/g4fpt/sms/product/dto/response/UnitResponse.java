package com.g4fpt.sms.product.dto.response;

import com.g4fpt.sms.product.enums.UnitStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnitResponse {
    private Long id;
    private String name;
    private UnitStatus status;
}
