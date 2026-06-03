package com.g4fpt.sms.customer.repository;

import com.g4fpt.sms.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByPhone(String phone);
    boolean existsByPhone(String phone);

    List<Customer> findByCustomerCodeOrPhone(String customerCode, String phone);
}