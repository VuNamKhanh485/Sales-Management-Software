package com.g4fpt.sms.product.repository;

import com.g4fpt.sms.product.entity.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    Page<Unit> findByNameContainingIgnoreCase(String name, Pageable pageable);
    @Query("""
            SELECT CASE WHEN COUNT(pu) > 0 THEN true ELSE false END
            FROM ProductUnit pu
            WHERE pu.unit.id = :unitId
    """)
    boolean existInOrderTransaction(@Param("unitId") Long unitId);
}
