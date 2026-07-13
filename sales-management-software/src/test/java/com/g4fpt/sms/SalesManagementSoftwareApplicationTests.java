package com.g4fpt.sms;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import com.g4fpt.sms.employee.repository.EmployeeRepository;
import com.g4fpt.sms.employee.entity.Employee;

@SpringBootTest
class SalesManagementSoftwareApplicationTests {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void contextLoads() {
        System.out.println("=== DEBBUGING EMPLOYEE CREDENTIALS ===");
        for (Employee emp : employeeRepository.findAll()) {
            System.out.println("Employee: " + emp.getFullName() +
                    " | Email: " + emp.getEmail() +
                    " | Role: " + (emp.getRole() != null ? emp.getRole().getCode() : "null"));
        }
        System.out.println("=======================================");
    }

}
