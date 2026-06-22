package com.g4fpt.sms.employee.service.impl;

import com.g4fpt.sms.employee.repository.EmployeeRepository;
import com.g4fpt.sms.employee.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl {

    private EmployeeRepository repository;

    public EmployeeServiceImpl(EmployeeRepository repository) {
        this.repository = repository;
    }


}
