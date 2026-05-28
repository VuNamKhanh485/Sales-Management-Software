package com.g4fpt.sms.service;

import com.g4fpt.sms.dto.request.BranchRequest;
import com.g4fpt.sms.entity.Branch;

import java.util.List;

public interface BranchService {

    Branch create(BranchRequest request);

    List<Branch> getAll();

    Branch getById(Long id);

    Branch update(Long id, BranchRequest request);

    void delete(Long id);
}