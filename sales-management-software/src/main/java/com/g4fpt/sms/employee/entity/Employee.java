package com.g4fpt.sms.employee.entity;

import com.g4fpt.sms.employee.utils.WorkStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "`Employee`")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "role_id")
    private Long roleId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="role_id", insertable = false, updatable = false)
    private Role role;

    @Column(name = "employee_code", nullable = false)
    private String employeeCode;

    @Column(name = "fullname", nullable = false)
    private String fullname;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_status")
    private WorkStatus workStatus;



}
