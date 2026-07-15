package com.g4fpt.sms.customer.repository;

import com.g4fpt.sms.customer.entity.CustomerRank;
import com.g4fpt.sms.customer.enums.CustomerStatus;
import java.math.BigDecimal;

public interface CustomerProjection {
    Long getId();
    String getFullName();
    String getPhone();
    CustomerRank getCustomerRank();
    String getEmail();
    BigDecimal getTotalRevenue();
    CustomerStatus getStatus();
}

