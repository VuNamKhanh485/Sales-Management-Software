package com.g4fpt.sms.employee.service.impl;

import com.g4fpt.sms.auth.dto.SessionUser;
import com.g4fpt.sms.auth.security.PasswordUtil;
import com.g4fpt.sms.branch.entity.Branch;
import com.g4fpt.sms.branch.repository.BranchRepository;
import com.g4fpt.sms.employee.dto.EmployeeForm;
import com.g4fpt.sms.employee.entity.Employee;
import com.g4fpt.sms.employee.entity.Role;
import com.g4fpt.sms.employee.repository.EmployeeRepository;
import com.g4fpt.sms.employee.repository.RoleRepository;
import com.g4fpt.sms.employee.service.EmployeeService;
import com.g4fpt.sms.employee.utils.WorkStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final BranchRepository branchRepository;
    private final PasswordUtil passwordUtil;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               RoleRepository roleRepository,
                               BranchRepository branchRepository,
                               PasswordUtil passwordUtil) {
        this.employeeRepository = employeeRepository;
        this.roleRepository = roleRepository;
        this.branchRepository = branchRepository;
        this.passwordUtil = passwordUtil;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Employee> searchEmployees(String keyword,
                                          Long branchId,
                                          Long roleId,
                                          WorkStatus status,
                                          Pageable pageable,
                                          SessionUser currentUser) {

        checkCanAccessEmployeeModule(currentUser);

        if ("BRANCH_MANAGER".equals(currentUser.getRoleCode())) {
            branchId = currentUser.getBranchId();
        }

        return employeeRepository.searchEmployees(keyword, branchId, roleId, status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeForm getFormById(Long id, SessionUser currentUser) {
        Employee employee = findById(id);
        checkCanManageEmployee(employee, currentUser);

        EmployeeForm form = new EmployeeForm();
        form.setId(employee.getId());
        form.setEmployeeCode(employee.getEmployeeCode());
        form.setFullName(employee.getFullName());
        form.setEmail(employee.getEmail());
        form.setPhone(employee.getPhone());
        form.setAddress(employee.getAddress());
        form.setGender(employee.getGender());
        form.setDob(employee.getDob());
        form.setHiredDate(employee.getHiredDate());
        form.setBaseSalary(employee.getBaseSalary());
        form.setWorkStatus(employee.getWorkStatus());
        form.setNote(employee.getNote());

        if (employee.getBranch() != null) {
            form.setBranchId(employee.getBranch().getId());
        }

        if (employee.getRole() != null) {
            form.setRoleId(employee.getRole().getId());
        }

        return form;
    }

    @Override
    public void create(EmployeeForm form, SessionUser currentUser) {
        checkCanAccessEmployeeModule(currentUser);

        if (employeeRepository.existsByEmployeeCode(form.getEmployeeCode())) {
            throw new RuntimeException("Mã nhân viên đã tồn tại");
        }

        if (employeeRepository.existsByEmail(form.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        Role role = roleRepository.findById(form.getRoleId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò"));

        if ("BRANCH_MANAGER".equals(currentUser.getRoleCode())
                && "OWNER".equals(role.getCode())) {
            throw new RuntimeException("BRANCH_MANAGER không được tạo nhân viên OWNER");
        }

        Branch branch;

        if ("OWNER".equals(currentUser.getRoleCode())) {
            branch = branchRepository.findById(form.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chi nhánh"));
        } else {
            branch = branchRepository.findById(currentUser.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chi nhánh của bạn"));
        }

        if (form.getPassword() == null || form.getPassword().isBlank()) {
            throw new RuntimeException("Mật khẩu không được để trống khi thêm nhân viên");
        }

        Employee employee = new Employee();
        employee.setEmployeeCode(form.getEmployeeCode());
        employee.setFullName(form.getFullName());
        employee.setEmail(form.getEmail());
        employee.setPasswordHash(passwordUtil.hash(form.getPassword()));
        employee.setPhone(form.getPhone());
        employee.setAddress(form.getAddress());
        employee.setGender(form.getGender());
        employee.setDob(form.getDob());
        employee.setHiredDate(form.getHiredDate());
        employee.setBaseSalary(form.getBaseSalary());
        employee.setWorkStatus(form.getWorkStatus());
        employee.setNote(form.getNote());
        employee.setBranch(branch);
        employee.setRole(role);

        employeeRepository.save(employee);
    }

    @Override
    public void update(Long id, EmployeeForm form, SessionUser currentUser) {
        Employee employee = findById(id);
        checkCanManageEmployee(employee, currentUser);

        if (employeeRepository.existsByEmployeeCodeAndIdNot(form.getEmployeeCode(), id)) {
            throw new RuntimeException("Mã nhân viên đã tồn tại");
        }

        if (employeeRepository.existsByEmailAndIdNot(form.getEmail(), id)) {
            throw new RuntimeException("Email đã tồn tại");
        }

        Role role = roleRepository.findById(form.getRoleId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò"));

        if ("BRANCH_MANAGER".equals(currentUser.getRoleCode())
                && "OWNER".equals(role.getCode())) {
            throw new RuntimeException("BRANCH_MANAGER không được sửa nhân viên thành OWNER");
        }

        Branch branch;

        if ("OWNER".equals(currentUser.getRoleCode())) {
            branch = branchRepository.findById(form.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chi nhánh"));
        } else {
            branch = branchRepository.findById(currentUser.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chi nhánh của bạn"));
        }

        employee.setEmployeeCode(form.getEmployeeCode());
        employee.setFullName(form.getFullName());
        employee.setEmail(form.getEmail());
        employee.setPhone(form.getPhone());
        employee.setAddress(form.getAddress());
        employee.setGender(form.getGender());
        employee.setDob(form.getDob());
        employee.setHiredDate(form.getHiredDate());
        employee.setBaseSalary(form.getBaseSalary());
        employee.setWorkStatus(form.getWorkStatus());
        employee.setNote(form.getNote());
        employee.setBranch(branch);
        employee.setRole(role);

        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            employee.setPasswordHash(passwordUtil.hash(form.getPassword()));
        }

        employeeRepository.save(employee);
    }

    @Override
    public void toggleStatus(Long id, SessionUser currentUser) {
        Employee employee = findById(id);
        checkCanManageEmployee(employee, currentUser);

        if (employee.getWorkStatus() == WorkStatus.ACTIVE) {
            employee.setWorkStatus(WorkStatus.INACTIVE);
        } else {
            employee.setWorkStatus(WorkStatus.ACTIVE);
        }

        employeeRepository.save(employee);
    }

    @Override
    public void delete(Long id, SessionUser currentUser) {
        Employee employee = findById(id);
        checkCanManageEmployee(employee, currentUser);

        employeeRepository.delete(employee);
    }

    private void checkCanAccessEmployeeModule(SessionUser user) {
        if (user == null) {
            throw new RuntimeException("Bạn chưa đăng nhập");
        }

        String roleCode = user.getRoleCode();

        if (!"OWNER".equals(roleCode) && !"BRANCH_MANAGER".equals(roleCode)) {
            throw new RuntimeException("Bạn không có quyền truy cập quản lý nhân viên");
        }
    }

    private void checkCanManageEmployee(Employee employee, SessionUser user) {
        checkCanAccessEmployeeModule(user);

        if ("BRANCH_MANAGER".equals(user.getRoleCode())) {
            if (employee.getBranch() == null
                    || !employee.getBranch().getId().equals(user.getBranchId())) {
                throw new RuntimeException("Bạn không được quản lý nhân viên khác chi nhánh");
            }

            if (employee.getRole() != null
                    && "OWNER".equals(employee.getRole().getCode())) {
                throw new RuntimeException("BRANCH_MANAGER không được quản lý OWNER");
            }
        }
    }
}