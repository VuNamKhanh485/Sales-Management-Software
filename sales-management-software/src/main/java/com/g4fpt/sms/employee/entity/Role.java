package com.g4fpt.sms.employee.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "Role")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor

public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="code",nullable = false,unique = true,length = 50)
    private String code;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name = "description")
    @Lob
    private String description;
}
