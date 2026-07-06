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

    boolean existsByPhoneAndIdNot(String phone, Long id);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    @Query(value = "SELECT c.id AS id, c.fullName AS fullName, c.phone AS phone, c.customerRank AS customerRank, c.email AS email, c.totalRevenue AS totalRevenue, c.status AS status " +
           "FROM Customer c WHERE c.phone LIKE CONCAT('%', :keyword, '%') OR LOWER(FUNCTION('regexp_replace', c.fullName, '[[:space:]]+', ' ')) LIKE LOWER(CONCAT('%', :keyword, '%'))",
           countQuery = "SELECT count(c) FROM Customer c WHERE c.phone LIKE CONCAT('%', :keyword, '%') OR LOWER(FUNCTION('regexp_replace', c.fullName, '[[:space:]]+', ' ')) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<CustomerProjection> searchByPhoneOrName(@Param("keyword") String keyword, Pageable pageable); 

    @Query("SELECT c.id AS id, c.fullName AS fullName, c.phone AS phone, c.customerRank AS customerRank " +
           "FROM Customer c WHERE c.status = com.g4fpt.sms.customer.enums.CustomerStatus.ACTIVE " +
           "AND (c.phone LIKE CONCAT('%', :keyword, '%') OR LOWER(FUNCTION('regexp_replace', c.fullName, '[[:space:]]+', ' ')) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<CustomerProjection> searchActiveByPhoneOrName(@Param("keyword") String keyword, Pageable pageable);

    Page<CustomerProjection> findAllProjectedBy(Pageable pageable);
}
