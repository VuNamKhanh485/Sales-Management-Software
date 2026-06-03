package com.g4fpt.sms.branch.service.impl;

import com.g4fpt.sms.branch.dto.BranchRequest;
import com.g4fpt.sms.branch.entity.Branch;
import com.g4fpt.sms.branch.repository.BranchRepository;
import com.g4fpt.sms.branch.service.BranchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    public BranchServiceImpl(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    public List<Branch> getAll() {
        return branchRepository.findAll();
    }

    @Override
    public Branch getById(Long id) {
        Optional<Branch> optionalBranch = branchRepository.findById(id);

        if (optionalBranch.isPresent()) return optionalBranch.get();

        throw new RuntimeException("Branch not found");
    }

    @Override
    public void create(BranchRequest request) {
        Branch branch = new Branch();

        branch.setBranchCode(request.getBranchCode());
        branch.setName(request.getName());
        branch.setPhone(request.getPhone());
        branch.setEmail(request.getEmail());
        branch.setAddress(request.getAddress());
        branch.setStatus(request.getStatus());
        branch.setOpenedAt(request.getOpenedAt());
        branch.setClosedAt(request.getClosedAt());
        branch.setNote(request.getNote());

        branchRepository.save(branch);
    }

    @Override
    public void update(Long id, BranchRequest request) {
        Branch branch = getById(id);

        branch.setName(request.getName());
        branch.setPhone(request.getPhone());
        branch.setEmail(request.getEmail());
        branch.setAddress(request.getAddress());
        branch.setStatus(request.getStatus());
        branch.setOpenedAt(request.getOpenedAt());
        branch.setClosedAt(request.getClosedAt());
        branch.setNote(request.getNote());

        branchRepository.save(branch);
    }

    @Override
    public void delete(Long id) {
        branchRepository.deleteById(id);
    }
}
