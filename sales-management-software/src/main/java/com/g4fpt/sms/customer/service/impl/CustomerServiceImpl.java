package com.g4fpt.sms.customer.service.impl;

import com.g4fpt.sms.customer.dto.CustomerRequestDTO;
import com.g4fpt.sms.customer.entity.Customer;
import com.g4fpt.sms.customer.entity.CustomerRank;
import com.g4fpt.sms.customer.enums.CustomerStatus;
import com.g4fpt.sms.customer.repository.CustomerRankRepository;
import com.g4fpt.sms.customer.repository.CustomerRepository;
import com.g4fpt.sms.customer.service.CustomerService;
import com.g4fpt.sms.employee.entity.Employee;
import com.g4fpt.sms.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerRankRepository customerRankRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public Customer createCustomer(CustomerRequestDTO dto, Long createdById) {
        if (customerRepository.existsByPhone(dto.getPhone())) {
            throw new RuntimeException("Số điện thoại đã tồn tại trong hệ thống!");
        }

        Customer customer = new Customer();
        customer.setFullName(dto.getFullName());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());
        customer.setGender(dto.getGender());
        customer.setDob(dto.getDob());
        customer.setNote(dto.getNote());


        customer.setCustomerCode("CUS-" + System.currentTimeMillis());


        if (dto.getCustomerRankId() != null) {
            CustomerRank rank = customerRankRepository.findById(dto.getCustomerRankId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hạng thẻ"));
            customer.setCustomerRank(rank);
        }

        if (createdById != null) {
            Employee creator = employeeRepository.findById(createdById).orElse(null);
            customer.setCreatedBy(creator);
        }
        customer.setStatus(dto.getStatus() != null ? dto.getStatus() : CustomerStatus.ACTIVE);
        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public Customer updateCustomer(Long id, CustomerRequestDTO dto, Long updatedById) {
        Customer customer = getCustomerById(id);

        if (!customer.getPhone().equals(dto.getPhone()) && customerRepository.existsByPhone(dto.getPhone())) {
            throw new RuntimeException("Số điện thoại mới đã được sử dụng bởi khách hàng khác!");
        }

        customer.setFullName(dto.getFullName());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());
        customer.setGender(dto.getGender());
        customer.setDob(dto.getDob());
        customer.setNote(dto.getNote());
        
        if (dto.getCustomerRankId() != null) {
            CustomerRank rank = customerRankRepository.findById(dto.getCustomerRankId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hạng thẻ"));
            customer.setCustomerRank(rank);
        } else {
            customer.setCustomerRank(null);
        }

        Employee updater = new Employee();
        updater.setId(updatedById);
        customer.setUpdatedBy(updater);
        customer.setStatus(dto.getStatus() != null ? dto.getStatus() : CustomerStatus.ACTIVE);
        return customerRepository.save(customer);
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
    }

    @Override
    public Page<Customer> getCustomers(String keyword, int page, int size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

        if (keyword != null && !keyword.trim().isEmpty()) {
            String cleanKeyword = keyword.trim();
            return customerRepository.findByPhoneContainingOrFullNameContainingIgnoreCase(cleanKeyword, cleanKeyword, pageable);
        }

        return customerRepository.findAll(pageable);
    }



































//    @Override
//    @Transactional
//    public Customer save(CustomerRequestDTO dto, Long currentUserId) {
//
//        Customer customer;
//
//        if (dto.getId() == null) {
//            // ===== THÊM MỚI =====
//            if (customerRepository.existsByPhone(dto.getPhone())) {
//                throw new RuntimeException("Số điện thoại đã tồn tại trong hệ thống!");
//            }
//            customer = new Customer();
//            customer.setCustomerCode("CUS-" + System.currentTimeMillis());
//
//            // set người tạo
//            if (currentUserId != null) {
//                Employee creator = employeeRepository.findById(currentUserId).orElse(null);
//                customer.setCreatedBy(creator);
//            }
//        } else {
//            // ===== CẬP NHẬT =====
//            customer = getCustomerById(dto.getId());
//
//            if (!customer.getPhone().equals(dto.getPhone())
//                    && customerRepository.existsByPhone(dto.getPhone())) {
//                throw new RuntimeException("Số điện thoại đã được sử dụng bởi khách hàng khác!");
//            }
//
//            // set người cập nhật
//            Employee updater = employeeRepository.findById(currentUserId).orElse(null);
//            customer.setUpdatedBy(updater);
//        }
//
//        // ===== PHẦN CHUNG (thêm và sửa đều dùng) =====
//        customer.setFullName(dto.getFullName());
//        customer.setPhone(dto.getPhone());
//        customer.setEmail(dto.getEmail());
//        customer.setAddress(dto.getAddress());
//        customer.setGender(dto.getGender());
//        customer.setDob(dto.getDob());
//        customer.setNote(dto.getNote());
//        customer.setStatus(dto.getStatus() != null ? dto.getStatus() : CustomerStatus.ACTIVE);
//
//        if (dto.getCustomerRankId() != null) {
//            CustomerRank rank = customerRankRepository.findById(dto.getCustomerRankId())
//                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hạng thẻ"));
//            customer.setCustomerRank(rank);
//        } else {
//            customer.setCustomerRank(null);
//        }
//
//        return customerRepository.save(customer);
//    }
}