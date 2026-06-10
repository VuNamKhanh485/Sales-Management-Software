package com.g4fpt.sms.employee.service;

import com.g4fpt.sms.auth.security.CustomUserDetails;
import com.g4fpt.sms.employee.entity.Employee;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    Optional<Employee> findEmployeeByEmail(String email);
    List<Employee> getAll(CustomUserDetails userDetails);
    Employee getById(Long id);
    Employee save(Employee employee);
    void delete(Long id);
    List<Employee> search(String keyword,CustomUserDetails currentUser);
}
