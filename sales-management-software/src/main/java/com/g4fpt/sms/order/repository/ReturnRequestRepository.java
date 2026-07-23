package com.g4fpt.sms.order.repository;

import com.g4fpt.sms.order.entity.ReturnRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {
    List<ReturnRequest> findByStatusOrderByCreatedAtDesc(String status);

    List<ReturnRequest> findAllByOrderByCreatedAtDesc();

    long countByStatus(String status);
}
