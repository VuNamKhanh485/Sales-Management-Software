package com.g4fpt.sms.employee.service;

import com.g4fpt.sms.auth.dto.SessionUser;
import com.g4fpt.sms.employee.dto.EmployeeForm;
import com.g4fpt.sms.employee.entity.Employee;
import com.g4fpt.sms.employee.utils.WorkStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {

    Page<Employee> searchEmployees(
            String keyword,
            Long branchId,
            Long roleId,
            WorkStatus status,
            Pageable pageable,
            SessionUser currentUser);

    Employee findById(Long id);

    EmployeeForm getFormById(Long id, SessionUser currentUser);

    void create(EmployeeForm form, SessionUser currentUser);

    void update(Long id, EmployeeForm form, SessionUser currentUser);

    void toggleStatus(Long id, SessionUser currentUser);

    void delete(Long id, SessionUser currentUser);
}