package com.g4fpt.sms.customer.repository;

import com.g4fpt.sms.customer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.g4fpt.sms.customer.enums.CustomerStatus;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByPhone(String phone);

    @Query(value = "SELECT * FROM Customer c WHERE c.phone LIKE CONCAT('%', :keyword, '%') OR LOWER(c.full_name) LIKE LOWER(CONCAT('%', :keyword, '%'))",
           countQuery = "SELECT count(*) FROM Customer c WHERE c.phone LIKE CONCAT('%', :keyword, '%') OR LOWER(c.full_name) LIKE LOWER(CONCAT('%', :keyword, '%'))",
           nativeQuery = true)
    Page<Customer> searchByPhoneOrName(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT * FROM Customer c WHERE c.status = :status AND (c.phone LIKE CONCAT('%', :keyword, '%') OR LOWER(c.full_name) LIKE LOWER(CONCAT('%', :keyword, '%')))",
           countQuery = "SELECT count(*) FROM Customer c WHERE c.status = :status AND (c.phone LIKE CONCAT('%', :keyword, '%') OR LOWER(c.full_name) LIKE LOWER(CONCAT('%', :keyword, '%')))",
           nativeQuery = true)
    Page<Customer> searchActiveByPhoneOrName(@Param("status") String status, @Param("keyword") String keyword, Pageable pageable);
}