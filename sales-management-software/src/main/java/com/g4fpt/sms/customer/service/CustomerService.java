package com.g4fpt.sms.customer.service;

import com.g4fpt.sms.customer.dto.CustomerRequestDTO;
import com.g4fpt.sms.customer.entity.Customer;
import com.g4fpt.sms.customer.repository.CustomerProjection;
import org.springframework.data.domain.Page;

public interface CustomerService {
    // Customer saveCustomer(CustomerRequestDTO dto, Long operatorId);
    Customer createCustomer(CustomerRequestDTO requestDTO, Long createdById);
    Customer updateCustomer(Long id, CustomerRequestDTO requestDTO, Long updatedById);
    Customer getCustomerById(Long id);
    Page<CustomerProjection> getCustomers(String keyword, int page, int size);
    void updateCustomerRank(Long customerId);
}