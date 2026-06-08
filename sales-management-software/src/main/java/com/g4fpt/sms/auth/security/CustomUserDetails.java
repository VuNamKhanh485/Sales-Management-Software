package com.g4fpt.sms.auth.security;

import com.g4fpt.sms.employee.entity.Employee;
import com.g4fpt.sms.employee.utils.WorkStatus;
import lombok.Getter;
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

    public  boolean hasRole(String role){
        return employee.getRole().getCode().equals(role);

    }
    public Long getBranchId(){
        if(employee.getBranch() == null){
            return null;
        }
        return employee.getBranch().getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return List.of(new SimpleGrantedAuthority("ROLE_"+employee.getRole().getCode()));
    }

    @Override
    public String getPassword() {
        return employee.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return employee.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return employee.getWorkStatus() == WorkStatus.ACTIVE;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return employee.getWorkStatus() == WorkStatus.ACTIVE;
    }
}
