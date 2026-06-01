package com.g4fpt.sms.voucher.repository;

import com.g4fpt.sms.voucher.entity.Voucher;
import com.g4fpt.sms.voucher.enums.VoucherStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    @Query("SELECT v FROM Voucher v WHERE " +
            "(:keyword IS NULL OR LOWER(v.code) LIKE LOWER(CONCAT('%',:keyword,'%')) " +
            "OR LOWER(v.nameVoucher) LIKE LOWER(CONCAT('%',:keyword,'%'))) " +
            "AND (:status IS NULL OR v.status = :status) " +
            "ORDER BY v.createdAt DESC")
    List<Voucher> search(
            @Param("keyword") String keyword,
            @Param("status") VoucherStatus status
    );
}