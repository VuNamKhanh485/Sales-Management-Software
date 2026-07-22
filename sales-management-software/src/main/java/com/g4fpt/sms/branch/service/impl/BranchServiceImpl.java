package com.g4fpt.sms.branch.service.impl;

import com.g4fpt.sms.branch.dto.BranchRequest;
import com.g4fpt.sms.branch.entity.Branch;
import com.g4fpt.sms.branch.repository.BranchRepository;
import com.g4fpt.sms.branch.service.BranchService;
import com.g4fpt.sms.employee.repository.EmployeeRepository;
import com.g4fpt.sms.inventory.repository.InventoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final EmployeeRepository employeeRepository;
    private final InventoryRepository inventoryRepository;

    public BranchServiceImpl(BranchRepository branchRepository, EmployeeRepository employeeRepository, InventoryRepository inventoryRepository) {
        this.branchRepository = branchRepository;
        this.employeeRepository = employeeRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public List<Branch> getAll() {
        return branchRepository.findAll();
    }

    @Override
    public Page<Branch> getAll(Pageable pageable) {
        return branchRepository.findAll(pageable);
    }

    @Override
    public Branch getById(Long id) {
        Optional<Branch> optionalBranch = branchRepository.findById(id);

        if (optionalBranch.isPresent()) return optionalBranch.get();

        throw new RuntimeException("Branch not found");
    }

    @Override
    public void create(BranchRequest request) {

        validateCreateRequest(request);

        Branch branch = new Branch();

        branch.setBranchCode(request.getBranchCode());
        branch.setName(request.getName());
        branch.setPhone(request.getPhone());
        branch.setEmail(request.getEmail());
        branch.setAddress(request.getAddress());
        branch.setStatus(request.getStatus());
        branch.setNote(request.getNote());

        branchRepository.save(branch);
    }

    @Override
    public void update(Long id, BranchRequest request) {

        validateUpdateRequest(id, request);

        Branch branch = getById(id);

        branch.setName(request.getName());
        branch.setPhone(request.getPhone());
        branch.setEmail(request.getEmail());
        branch.setAddress(request.getAddress());
        branch.setStatus(request.getStatus());
        branch.setNote(request.getNote());

        branchRepository.save(branch);
    }

    @Override
    public void delete(Long id) {
        if (inventoryRepository.existsByBranchIdAndStockGreaterThan(id, 0)) {
            throw new RuntimeException("Không thể xóa chi nhánh vì vẫn còn hàng hóa tồn kho.");
        }

        if (employeeRepository.existsByBranchId(id)) {
            throw new RuntimeException("Không thể xóa chi nhánh vì vẫn còn nhân viên trực thuộc.");
        }

        branchRepository.deleteById(id);
    }


    private void validateCreateRequest(BranchRequest request) {

        List<String> errors = new ArrayList<>();

        if (branchRepository.existsByBranchCode(request.getBranchCode())) {
            errors.add("Mã chi nhánh đã tồn tại");
        }

        if (branchRepository.existsByPhone(request.getPhone())) {
            errors.add("Số điện thoại đã tồn tại");
        }

        if (branchRepository.existsByEmail(request.getEmail())) {
            errors.add("Email đã tồn tại");
        }

        if (!errors.isEmpty()) {
            throw new RuntimeException(String.join("|", errors));
        }

    }

    private void validateUpdateRequest(Long id, BranchRequest request) {

        List<String> errors = new ArrayList<>();

        if (branchRepository.existsByPhoneAndIdNot(request.getPhone(), id)) {
            errors.add("Số điện thoại đã tồn tại");
        }

        if (branchRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            errors.add("Email đã tồn tại");
        }

        if (!errors.isEmpty()) {
            throw new RuntimeException(String.join("|", errors));
        }

    }

    @Override
    public Page<Branch> search(String searchType, String keyword, Pageable pageable) {

        if ("branchCode".equals(searchType)) {
            return branchRepository.findByBranchCodeContainsIgnoreCase(keyword, pageable);
        }
        return branchRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }


}
