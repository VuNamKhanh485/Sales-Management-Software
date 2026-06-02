package com.g4fpt.sms.payment.repository;

import com.g4fpt.sms.payment.entity.PaymentMethod;
import com.g4fpt.sms.payment.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    @Query("SELECT p FROM PaymentMethod p WHERE " +
           "(:keyword IS NULL OR LOWER(p.code) LIKE LOWER(CONCAT('%',:keyword,'%')) " +
           "OR LOWER(p.name) LIKE LOWER(CONCAT('%',:keyword,'%'))) " +
           "AND (:status IS NULL OR p.status = :status) " +
           "ORDER BY p.createdAt DESC")
    List<PaymentMethod> search(
            @Param("keyword") String keyword,
            @Param("status") PaymentStatus status
    );
}
