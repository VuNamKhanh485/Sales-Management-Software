package com.g4fpt.sms.auth.service;

import com.g4fpt.sms.employee.entity.Employee;
import com.g4fpt.sms.employee.entity.Role;
import com.g4fpt.sms.employee.utils.WorkStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

    public class CustomUserDetails implements UserDetails {

    private Employee employee;

    public CustomUserDetails(Employee employee){
        this.employee = employee;
    }
    public Employee getEmployee() {
        return employee;
    }

    public Long getId() {
        return employee.getId();
    }

    public String getFullname() {
        return employee.getFullname();
    }
    @Override
    public String getUsername() {
        return employee.getEmail();
    }

    @Override
    public String getPassword() {
        return employee.getPassword();
    }
    @Override
    public boolean isEnabled() {
        return employee.getWorkStatus() == WorkStatus.ACTIVE;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Role role = employee.getRole();
        if (role == null || role.getCode() == null || role.getCode().isBlank()) {
            return List.of();
        }

        return List.of(
                new SimpleGrantedAuthority("ROLE_" + role.getCode())
        );
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
