package com.g4fpt.sms.customer.repository;

import com.g4fpt.sms.customer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByPhone(String phone);

    Page<Customer> findByPhoneContainingOrFullNameContainingIgnoreCase(String phone, String fullName, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE c.status = com.g4fpt.sms.customer.enums.CustomerStatus.ACTIVE " +
           "AND (c.phone LIKE %:keyword% OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Customer> findActiveByPhoneOrName(@Param("keyword") String keyword, Pageable pageable);
}