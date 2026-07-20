package com.g4fpt.sms.auth.service;

import com.g4fpt.sms.auth.dto.SessionUser;

import java.util.Optional;

public interface AuthService {
    Optional<SessionUser> authenticate(String email, String rawPassword);
}
