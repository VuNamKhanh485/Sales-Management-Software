package com.g4fpt.sms.supplier.dto.request;

import com.g4fpt.sms.supplier.enums.SupplierStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplierRequest {
    private String code;
    private String name;
    private String phone;
    private String email;
    private String address;
    private SupplierStatus status;
    private String note;
}
