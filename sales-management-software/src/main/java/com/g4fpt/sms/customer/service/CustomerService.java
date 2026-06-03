package com.g4fpt.sms.customer.service;

import com.g4fpt.sms.customer.dto.CustomerRequest;
import com.g4fpt.sms.customer.entity.Customer;
import java.util.List;

public interface CustomerService {
    List<Customer> getAllCustomers();
    Customer getCustomerById(Long id);
    Customer createCustomer(CustomerRequest request);
    Customer updateCustomer(Long id, CustomerRequest request);
    void toggleStatus(Long id);
}