package com.g4fpt.sms.employee.entity;

import com.g4fpt.sms.branch.entity.Branch;
import com.g4fpt.sms.employee.utils.Gender;
import com.g4fpt.sms.employee.utils.WorkStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "Employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;


    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Employee createdBy;


    @Column(name = "employee_code", nullable = false, unique = true, length = 100)
    private String employeeCode;


    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;


    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;


    @Column(name = "phone", length = 15)
    private String phone;


    @Column(name = "address", length = 255)
    private String address;


    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;


    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "hired_date", nullable = false)
    private LocalDate hiredDate;


    @Column(name = "base_salary", precision = 12, scale = 2)
    private BigDecimal baseSalary;


    @Enumerated(EnumType.STRING)
    @Column(name = "work_status", nullable = false)
    private WorkStatus workStatus = WorkStatus.ACTIVE;


    @Column(name = "note", columnDefinition = "TEXT")
    private String note;


    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}

