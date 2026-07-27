package com.g4fpt.sms.customer.service.impl;

import com.g4fpt.sms.customer.dto.CustomerRequestDTO;
import com.g4fpt.sms.customer.entity.Customer;
import com.g4fpt.sms.customer.entity.CustomerRank;
import com.g4fpt.sms.customer.enums.CustomerStatus;
import com.g4fpt.sms.customer.repository.CustomerRankRepository;
import com.g4fpt.sms.customer.repository.CustomerRepository;
import com.g4fpt.sms.customer.repository.CustomerProjection;
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
import com.g4fpt.sms.order.repository.OrderTransactionRepository;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerRankRepository customerRankRepository;
    private final EmployeeRepository employeeRepository;
    private final OrderTransactionRepository orderTransactionRepository;

    private void validateCustomerUniqueness(CustomerRequestDTO dto, Long id) {
        String phone = dto.getPhone();
        String email = dto.getEmail();

        if (phone != null && !phone.trim().isEmpty()) {
            if (!phone.trim().matches("^\\d{10,11}$")) {
                throw new RuntimeException("Số điện thoại chỉ được nhập số và phải từ 10-11 số!");
            }
            boolean phoneExists = (id == null) 
                ? customerRepository.existsByPhone(phone.trim()) 
                : customerRepository.existsByPhoneAndIdNot(phone.trim(), id);
            if (phoneExists) {
                throw new RuntimeException("Số điện thoại đã tồn tại trong hệ thống!");
            }
        }

        if (email != null && !email.trim().isEmpty()) {
            boolean emailExists = (id == null) 
                ? customerRepository.existsByEmail(email.trim()) 
                : customerRepository.existsByEmailAndIdNot(email.trim(), id);
            if (emailExists) {
                throw new RuntimeException("Email đã tồn tại trong hệ thống!");
            }
        }
    }

    @Override
    @Transactional
    public Customer createCustomer(CustomerRequestDTO dto, Long createdById) {
        validateCustomerUniqueness(dto, null);

        Customer customer = new Customer();
        customer.setFullName(dto.getFullName());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());
        customer.setGender(dto.getGender());
        customer.setDob(dto.getDob());
        customer.setNote(dto.getNote());

        customer.setCustomerCode("CUS-" + System.currentTimeMillis());

        CustomerRank rank = determineRank(BigDecimal.ZERO, 0L);
        customer.setCustomerRank(rank);

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
        validateCustomerUniqueness(dto, id);

        customer.setFullName(dto.getFullName());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());
        customer.setGender(dto.getGender());
        customer.setDob(dto.getDob());
        customer.setNote(dto.getNote());

        long orderCount = orderTransactionRepository.countByCustomerId(customer.getId());
        CustomerRank rank = determineRank(customer.getTotalRevenue(), orderCount);
        customer.setCustomerRank(rank);

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
    public Page<CustomerProjection> getCustomers(String keyword, int page, int size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

        if (keyword != null && !keyword.trim().isEmpty()) {
            String cleanKeyword = keyword.trim().replaceAll("\\s+", " ");
            return customerRepository.searchByPhoneOrName(cleanKeyword, pageable);
        }

        return customerRepository.findAllProjectedBy(pageable);
    }

    @Override
    @Transactional
    public void updateCustomerRank(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null)
            return;

        long orderCount = orderTransactionRepository.countByCustomerId(customerId);
        CustomerRank rank = determineRank(customer.getTotalRevenue(), orderCount);
        customer.setCustomerRank(rank);
        customerRepository.save(customer);
    }

    private CustomerRank determineRank(BigDecimal totalRevenue, long orderCount) {
        String rankName;
        double revenue = totalRevenue != null ? totalRevenue.doubleValue() : 0.0;
        if (revenue < 5000000.0) {
            rankName = "Thường";
        } else if (revenue < 10000000.0) {
            rankName = "Bạc";
        } else if (revenue < 15000000.0) {
            rankName = "Vàng";
        } else {
            rankName = "Kim cương";
        }

        return customerRankRepository.findByName(rankName)
                .orElseGet(() -> {
                    return customerRankRepository.findAll().stream()
                            .filter(r -> r.getName().equalsIgnoreCase(rankName))
                            .findFirst()
                            .orElse(null);
                });
    }

}