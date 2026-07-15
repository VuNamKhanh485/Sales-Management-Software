package com.g4fpt.sms.auth.service;

import com.g4fpt.sms.auth.dto.SessionUser;
import com.g4fpt.sms.auth.security.PasswordUtil;
import com.g4fpt.sms.employee.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface AuthService {
    Optional<SessionUser> authenticate(String email, String rawPassword);
}
