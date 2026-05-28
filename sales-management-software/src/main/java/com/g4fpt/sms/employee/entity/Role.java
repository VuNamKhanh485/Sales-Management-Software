package com.g4fpt.sms.employee.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`Role`")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name ="code",nullable = false,length = 255)
    private String code;

    @Column(name="name",nullable = false,length = 255)
    private String name;

    @Column(name="description",columnDefinition = "TEXT")
    private String descripion;

    @OneToMany(mappedBy = "role")
    private List<Employee> employeeList = new ArrayList<>();



}
