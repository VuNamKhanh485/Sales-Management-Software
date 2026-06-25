package com.g4fpt.sms.customer.repository;

import com.g4fpt.sms.customer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.g4fpt.sms.customer.enums.CustomerStatus;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByPhone(String phone);

    Page<Customer> findByPhoneContainingOrFullNameContainingIgnoreCase(String phone, String fullName, Pageable pageable);

    Page<Customer> findByStatusAndPhoneContainingOrStatusAndFullNameContainingIgnoreCase(
            CustomerStatus status1, String phone,
            CustomerStatus status2, String fullName,
            Pageable pageable
    );
}