package com.g4fpt.sms.employee.service;

import com.g4fpt.sms.employee.entity.Employee;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    Optional<Employee> findEmployeeByEmail(String email);
    List<Employee> getAll();
    Employee getById(Long id);
    Employee save(Employee employee);
    void delete(Long id);
}
