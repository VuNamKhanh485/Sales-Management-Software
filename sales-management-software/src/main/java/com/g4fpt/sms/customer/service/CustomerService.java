package com.g4fpt.sms.customer.service;

import com.g4fpt.sms.customer.dto.CustomerRequestDTO;
import com.g4fpt.sms.customer.entity.Customer;
import org.springframework.data.domain.Page;

public interface CustomerService {
    Customer createCustomer(CustomerRequestDTO requestDTO, Long createdById);
    Customer updateCustomer(Long id, CustomerRequestDTO requestDTO, Long updatedById);
//    Customer save(CustomerRequestDTO dto, Long currentUserId);
    Customer getCustomerById(Long id);
    Page<Customer> getCustomers(String keyword, int page, int size);
}