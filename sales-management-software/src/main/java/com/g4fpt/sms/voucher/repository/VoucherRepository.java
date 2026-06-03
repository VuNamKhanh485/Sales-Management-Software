package com.g4fpt.sms.voucher.repository;

import com.g4fpt.sms.voucher.entity.Voucher;
import com.g4fpt.sms.voucher.enums.VoucherStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    Optional<Voucher> findByCode(String code);

    @Query("""
            SELECT v FROM Voucher v
            WHERE (:keyword IS NULL OR LOWER(v.code) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(v.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:status IS NULL OR v.status = :status)
            """)
    Page<Voucher> search(
            @Param("keyword") String keyword,
            @Param("status") VoucherStatus status,
            Pageable pageable
    );

    @Query("""
            SELECT v FROM Voucher v
            WHERE v.status = 'ACTIVE'
              AND v.startAt <= :now
              AND v.endAt >= :now
            """)
    Page<Voucher> findAllActive(@Param("now") LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT COUNT(*) > 0 FROM order_transaction WHERE voucher_id = :voucherId", nativeQuery = true)
    boolean isVoucherUsed(@Param("voucherId") Long voucherId);
}