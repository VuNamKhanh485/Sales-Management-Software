package com.g4fpt.sms.customer.dto;

import com.g4fpt.sms.customer.enums.CustomerStatus;
import com.g4fpt.sms.employee.utils.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CustomerRequestDTO {
    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private Gender gender;
    private LocalDate dob;
    private String note;
    private Long customerRankId;
    private CustomerStatus status = CustomerStatus.ACTIVE;
}