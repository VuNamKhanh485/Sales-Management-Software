package com.g4fpt.sms.branch.service;

import com.g4fpt.sms.branch.dto.BranchRequest;
import com.g4fpt.sms.branch.entity.Branch;

import java.util.List;

public interface BranchService {

    List<Branch> getAll();

    Branch getById(Long id);

    void create(BranchRequest request);

    void update(Long id, BranchRequest request);

    void delete(Long id);

    List<Branch> search(String searchType, String keyword);
}
