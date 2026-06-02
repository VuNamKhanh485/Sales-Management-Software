package com.g4fpt.sms.employee.repository;

import com.g4fpt.sms.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long> {

    Optional<Employee> findEmployeeByEmailIgnoreCase(String email);
    @Query("SELECT e FROM Employee e")
    List<Employee> findALL();
}
