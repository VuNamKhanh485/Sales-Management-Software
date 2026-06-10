package com.g4fpt.sms.employee.repository;

import ch.qos.logback.core.model.conditional.ElseModel;
import com.g4fpt.sms.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findEmployeeByEmail(String email);

    List<Employee> findByBranchId(long id);
    @Query("select e from Employee e where lower(e.fullName) like lower(concat('%', :keyword, '%')) OR lower(e.employeeCode) like lower(concat('%', :keyword, '%'))")
    List<Employee> findAllByKeyword(@Param("keyword") String keyword);

    @Query("""
                SELECT e FROM Employee e
                WHERE e.branch.id = :branchid
                AND (
                    LOWER(e.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            """)
    List<Employee> findEmployeesContainingIgnoreCase(@Param("keyword") String keyword, @Param("branchid") long branchId);


}