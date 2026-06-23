package com.g4fpt.sms.customer.repository;

import com.g4fpt.sms.customer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByPhone(String phone);


    Optional<Customer> findByPhone(String phone);
    Optional<Customer> findByFullName(String fullName);

    Page<Customer> findByPhoneContaining(String phone, Pageable pageable);

    Page<Customer> findByPhoneContainingOrFullNameContainingIgnoreCase(String phone, String fullName, Pageable pageable);

}