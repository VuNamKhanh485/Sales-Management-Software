package com.g4fpt.sms.branch.service;

import com.g4fpt.sms.branch.dto.request.BranchRequest;
import com.g4fpt.sms.branch.entity.Branch;

import java.util.List;

public interface BranchService {

    Branch create(BranchRequest request);

    List<Branch> getAll();

    Branch getById(Long id);

    Branch update(Long id, BranchRequest request);

    void delete(Long id);
}