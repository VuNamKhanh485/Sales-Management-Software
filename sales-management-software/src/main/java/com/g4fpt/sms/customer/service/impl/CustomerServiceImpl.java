package com.g4fpt.sms.customer.service.impl;

import com.g4fpt.sms.customer.dto.CustomerRequest;
import com.g4fpt.sms.customer.entity.Customer;
import com.g4fpt.sms.customer.entity.CustomerRank;
import com.g4fpt.sms.customer.enums.CustomerStatus;
import com.g4fpt.sms.customer.repository.CustomerRankRepository;
import com.g4fpt.sms.customer.repository.CustomerRepository;
import com.g4fpt.sms.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerRankRepository customerRankRepository;

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng!"));
    }

    @Override
    @Transactional
    public Customer createCustomer(CustomerRequest request) {
        if (customerRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Số điện thoại đã tồn tại!");
        }

        CustomerRank rank = null;
        if (request.getCustomerRankId() != null) {
            rank = customerRankRepository.findById(request.getCustomerRankId()).orElse(null);
        }

        String genCode = "CUS-" + System.currentTimeMillis();

        Customer customer = Customer.builder()
                .customerCode(genCode)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .note(request.getNote())
                .customerRank(rank)
                .build();

        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public Customer updateCustomer(Long id, CustomerRequest request) {
        Customer customer = getCustomerById(id);


        if (!customer.getPhone().equals(request.getPhone()) && customerRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Số điện thoại đã được người khác sử dụng!");
        }

        CustomerRank rank = null;
        if (request.getCustomerRankId() != null) {
            rank = customerRankRepository.findById(request.getCustomerRankId()).orElse(null);
        }

        customer.setFullName(request.getFullName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setNote(request.getNote());
        customer.setCustomerRank(rank);

        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void toggleStatus(Long id) {
        Customer customer = getCustomerById(id);
        if (customer.getStatus() == CustomerStatus.ACTIVE) {
            customer.setStatus(CustomerStatus.INACTIVE);
        } else {
            customer.setStatus(CustomerStatus.ACTIVE);
        }
        customerRepository.save(customer);
    }
}