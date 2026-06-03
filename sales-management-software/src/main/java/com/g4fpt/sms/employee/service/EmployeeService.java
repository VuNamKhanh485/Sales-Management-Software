package com.g4fpt.sms.employee.service;

import com.g4fpt.sms.employee.entity.Employee;

public interface EmployeeService {
    Employee findEmployeeByEmail(String email);
}
