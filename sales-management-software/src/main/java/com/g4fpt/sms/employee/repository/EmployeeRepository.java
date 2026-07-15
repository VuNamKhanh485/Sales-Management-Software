package com.g4fpt.sms.employee.repository;

import com.g4fpt.sms.employee.entity.Employee;
import com.g4fpt.sms.employee.utils.WorkStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("""
                SELECT e
                FROM Employee e
                LEFT JOIN FETCH e.role
                LEFT JOIN FETCH e.branch
                WHERE e.email = :email
            """)
    Optional<Employee> findByEmail(@Param("email") String email);

    boolean existsByEmployeeCode(String employeeCode);

    boolean existsByEmail(String email);

    boolean existsByEmployeeCodeAndIdNot(String employeeCode, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    @EntityGraph(attributePaths = {"role", "branch"})
    @Query("""
        SELECT e FROM Employee e
        WHERE
            (:branchId IS NULL OR e.branch.id = :branchId)
            AND (:roleId IS NULL OR e.role.id = :roleId)
            AND (:status IS NULL OR e.workStatus = :status)
            AND (
                :keyword IS NULL OR :keyword = ''
                OR LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(e.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(e.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
        ORDER BY e.createdAt DESC
    """)
    Page<Employee> searchEmployees(
            @Param("keyword") String keyword,
            @Param("branchId") Long branchId,
            @Param("roleId") Long roleId,
            @Param("status") WorkStatus status,
            Pageable pageable
    );

    Optional<Employee> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
