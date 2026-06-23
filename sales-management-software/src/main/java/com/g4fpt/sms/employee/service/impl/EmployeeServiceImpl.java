package com.g4fpt.sms.employee.service.impl;

import com.g4fpt.sms.employee.entity.Employee;
import com.g4fpt.sms.employee.repository.EmployeeRepository;
import com.g4fpt.sms.employee.service.EmployeeService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(EmployeeRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<Employee> findEmployeeByEmail(String email) {
        return repository.findEmployeeByEmail(email);
    }

    @Override
    public List<Employee> getAll() {
        return repository.findAll();
    }

    @Override
    public Employee getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Employee save(Employee employee) {
        if (employee.getId() == null) {
            employee.setCreatedAt(LocalDateTime.now());
            if (employee.getPasswordHash() != null && !employee.getPasswordHash().startsWith("$2a$")) {
                employee.setPasswordHash(passwordEncoder.encode(employee.getPasswordHash()));
            }
        } else {
            employee.setUpdatedAt(LocalDateTime.now());
            Employee existing = repository.findById(employee.getId()).orElse(null);
            if (existing != null) {
                if (employee.getPasswordHash() != null && !employee.getPasswordHash().isEmpty() && !employee.getPasswordHash().equals(existing.getPasswordHash())) {
                    employee.setPasswordHash(passwordEncoder.encode(employee.getPasswordHash()));
                } else {
                    employee.setPasswordHash(existing.getPasswordHash());
                }
                if (employee.getCreatedAt() == null) {
                    employee.setCreatedAt(existing.getCreatedAt());
                }
            }
        }
        return repository.save(employee);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
