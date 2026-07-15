package com.g4fpt.sms.auth.service.impl;

import com.g4fpt.sms.auth.dto.SessionUser;
import com.g4fpt.sms.auth.security.PasswordUtil;
import com.g4fpt.sms.auth.service.AuthService;
import com.g4fpt.sms.branch.entity.Branch;
import com.g4fpt.sms.employee.entity.Employee;
import com.g4fpt.sms.employee.entity.Role;
import com.g4fpt.sms.employee.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final EmployeeRepository employeeRepository;
    private final PasswordUtil passwordUtil;

    public AuthServiceImpl(EmployeeRepository employeeRepository,
                           PasswordUtil passwordUtil) {
        this.employeeRepository = employeeRepository;
        this.passwordUtil = passwordUtil;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SessionUser> authenticate(String email, String rawPassword) {
        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(email);

        if (optionalEmployee.isEmpty()) {
            return Optional.empty();
        }

        Employee employee = optionalEmployee.get();

        if (!employee.isActive()) {
            return Optional.empty();
        }

        if (!passwordUtil.matches(rawPassword, employee.getPasswordHash())) {
            return Optional.empty();
        }

        return Optional.of(toSessionUser(employee));
    }

    private SessionUser toSessionUser(Employee employee) {
        Role role = employee.getRole();
        Branch branch = employee.getBranch();

        Long branchId = null;
        String branchName = null;

        if (branch != null) {
            branchId = branch.getId();
            branchName = branch.getName();
        }

        String roleCode = null;
        String roleName = null;

        if (role != null) {
            roleCode = role.getCode();
            roleName = role.getName();
        }

        return new SessionUser(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getFullName(),
                employee.getEmail(),
                roleCode,
                roleName,
                branchId,
                branchName
        );
    }
}
