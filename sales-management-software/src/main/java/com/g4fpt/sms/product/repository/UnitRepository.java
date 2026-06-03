package com.g4fpt.sms.product.repository;

import com.g4fpt.sms.product.entity.ProductUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitRepository extends JpaRepository<ProductUnit, Long> {
}
