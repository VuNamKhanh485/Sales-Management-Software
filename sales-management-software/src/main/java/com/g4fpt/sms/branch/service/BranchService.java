package com.g4fpt.sms.branch.service;

import com.g4fpt.sms.branch.dto.BranchRequest;
import com.g4fpt.sms.branch.entity.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BranchService {

    List<Branch> getAll();

    Page<Branch> getAll(Pageable pageable);

    Branch getById(Long id);

    void create(BranchRequest request);

    void update(Long id, BranchRequest request);

    void delete(Long id);

    Page<Branch> search(String searchType, String keyword, Pageable pageable);


}
