package com.g4fpt.sms.auth.service;

import com.g4fpt.sms.auth.dto.LoginRequest;
import com.g4fpt.sms.employee.entity.Employee;
import com.g4fpt.sms.employee.repository.EmployeeRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final EmployeeRepository employeeRepository;

    public AuthService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public CustomUserDetails login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail() == null ? "" : loginRequest.getEmail().trim();
        String password = loginRequest.getPassword() == null ? "" : loginRequest.getPassword();

        Employee employee = employeeRepository.findEmployeeByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Email or password is invalid"));

        if (!password.equals(employee.getPassword())) {
            throw new RuntimeException("Email or password is invalid");
        }

        CustomUserDetails userDetails = new CustomUserDetails(employee);
        if (!userDetails.isEnabled()) {
            throw new DisabledException("Employee account is inactive");
        }

        return userDetails;
    }
}
