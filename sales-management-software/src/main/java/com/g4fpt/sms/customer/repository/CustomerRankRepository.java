package com.g4fpt.sms.customer.repository;

import com.g4fpt.sms.customer.entity.CustomerRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRankRepository extends JpaRepository<CustomerRank, Long> {
}