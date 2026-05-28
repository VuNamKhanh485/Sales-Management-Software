package com.g4fpt.sms.service.impl;

import com.g4fpt.sms.dto.request.BranchRequest;
import com.g4fpt.sms.entity.Branch;
import com.g4fpt.sms.repository.BranchRepository;
import com.g4fpt.sms.service.BranchService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    public BranchServiceImpl(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    // CREATE
    @Override
    public Branch create(BranchRequest request) {

        Branch branch = new Branch();

        branch.setName(request.getName());
        branch.setAddress(request.getAddress());
        branch.setStatus(request.getStatus());
        branch.setManagerId(request.getManagerId());

        branch.setCreateAt(LocalDateTime.now());

        return branchRepository.save(branch);
    }

    // GET ALL
    @Override
    public List<Branch> getAll() {
        return branchRepository.findAll();
    }

    // GET BY ID
    @Override
    public Branch getById(Long id) {
        return branchRepository.findById(id)
                .orElse(null);
    }

    // UPDATE
    @Override
    public Branch update(Long id, BranchRequest request) {

        Branch oldBranch =
                branchRepository.findById(id)
                        .orElse(null);

        if (oldBranch != null) {

            oldBranch.setName(request.getName());
            oldBranch.setAddress(request.getAddress());
            oldBranch.setStatus(request.getStatus());
            oldBranch.setManagerId(request.getManagerId());

            oldBranch.setUpdateAt(LocalDateTime.now());

            return branchRepository.save(oldBranch);
        }

        return null;
    }

    // DELETE
    @Override
    public void delete(Long id) {
        branchRepository.deleteById(id);
    }
}