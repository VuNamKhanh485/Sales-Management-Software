package com.g4fpt.sms.branch.service.impl;

import com.g4fpt.sms.branch.dto.BranchRequest;
import com.g4fpt.sms.branch.entity.Branch;
import com.g4fpt.sms.branch.repository.BranchRepository;
import com.g4fpt.sms.branch.service.BranchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

        validateCreateRequest(request);

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

        validateUpdateRequest(id, request);

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


    private void validateCreateRequest(BranchRequest request) {

        List<String> errors = new ArrayList<>();

        if (request.getBranchCode() == null ||
                request.getBranchCode().trim().isEmpty()) {

            errors.add("Mã chi nhánh không được để trống");
        }

        if (request.getName() == null ||
                request.getName().trim().isEmpty()) {

            errors.add("Tên chi nhánh không được để trống");
        }

        if (request.getPhone() == null ||
                request.getPhone().trim().isEmpty()) {

            errors.add("Số điện thoại không được để trống");
        }

        if (request.getEmail() == null ||
                request.getEmail().trim().isEmpty()) {

            errors.add("Email không được để trống");
        }

        if (request.getBranchCode() != null &&
                branchRepository.existsByBranchCode(request.getBranchCode())) {

            errors.add("Mã chi nhánh đã tồn tại");
        }

        if (request.getPhone() != null &&
                branchRepository.existsByPhone(request.getPhone())) {

            errors.add("Số điện thoại đã tồn tại");
        }

        if (request.getEmail() != null &&
                branchRepository.existsByEmail(request.getEmail())) {

            errors.add("Email đã tồn tại");
        }

        if (request.getOpenedAt() == null) {

            errors.add("Ngày mở cửa không được để trống");
        }

        if (request.getClosedAt() != null
                && request.getOpenedAt() != null
                && request.getClosedAt().isBefore(request.getOpenedAt())) {

            errors.add("Ngày đóng cửa phải sau ngày mở cửa");
        }

        if (!errors.isEmpty()) {
            throw new RuntimeException(
                    String.join("|", errors));
        }
    }

    private void validateUpdateRequest(
            Long id,
            BranchRequest request) {

        List<String> errors = new ArrayList<>();

        if (request.getName() == null || request.getName().trim().isEmpty()) {

            errors.add("Tên chi nhánh không được để trống");
        }

        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {

            errors.add("Số điện thoại không được để trống");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {

            errors.add("Email không được để trống");
        }

        if (request.getOpenedAt() == null) {
            errors.add("Ngày mở cửa không được để trống");
        }

        if (request.getPhone() != null &&
                !request.getPhone().trim().isEmpty() &&
                branchRepository.existsByPhoneAndIdNot(
                        request.getPhone(), id)) {

            errors.add("Số điện thoại đã tồn tại");
        }

        if (request.getEmail() != null &&
                !request.getEmail().trim().isEmpty() &&
                branchRepository.existsByEmailAndIdNot(
                        request.getEmail(), id)) {

            errors.add("Email đã tồn tại");
        }

        if (request.getClosedAt() != null
                && request.getOpenedAt() != null
                && request.getClosedAt().isBefore(
                request.getOpenedAt())) {

            errors.add("Ngày đóng cửa phải sau ngày mở cửa");
        }

        if (!errors.isEmpty()) {

            throw new RuntimeException(
                    String.join("|", errors));
        }
    }

}
