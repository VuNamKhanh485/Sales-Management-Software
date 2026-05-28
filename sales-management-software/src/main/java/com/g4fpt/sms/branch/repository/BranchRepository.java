package com.g4fpt.sms.branch.repository;

import com.g4fpt.sms.branch.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Long> {
}
