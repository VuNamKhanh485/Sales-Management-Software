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

    Page<Voucher> findByStatusAndCodeContainingIgnoreCaseOrStatusAndNameContainingIgnoreCase(
            VoucherStatus status1, String code,
            VoucherStatus status2, String name,
            Pageable pageable);

    Page<Voucher> findByCodeContainingIgnoreCaseOrNameContainingIgnoreCase(
            String code, String name, Pageable pageable);

    Page<Voucher> findByStatus(VoucherStatus status, Pageable pageable);

    Page<Voucher> findByStatusAndStartAtLessThanEqualAndEndAtGreaterThanEqual(
            VoucherStatus status, LocalDateTime startAt, LocalDateTime endAt, Pageable pageable);


}