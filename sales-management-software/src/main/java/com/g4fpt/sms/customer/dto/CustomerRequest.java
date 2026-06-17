package com.g4fpt.sms.customer.dto;

import lombok.Data;

@Data
public class CustomerRequest {
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private String note;
    private Long customerRankId;
}