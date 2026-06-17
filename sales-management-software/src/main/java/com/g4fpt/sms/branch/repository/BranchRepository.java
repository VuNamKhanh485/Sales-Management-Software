package com.g4fpt.sms.branch.repository;

import com.g4fpt.sms.branch.entity.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    boolean existsByBranchCode(String branchCode);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    Page<Branch> findByBranchCodeContainsIgnoreCase(String branchCode, Pageable pageable);

    Page<Branch> findByNameContainingIgnoreCase(String name, Pageable pageable);


}
