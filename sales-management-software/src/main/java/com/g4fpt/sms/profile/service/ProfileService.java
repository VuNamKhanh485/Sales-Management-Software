package com.g4fpt.sms.profile.service;

import com.g4fpt.sms.employee.entity.Employee;
import com.g4fpt.sms.profile.dto.ChangePasswordRequest;
import com.g4fpt.sms.profile.dto.ProfileUpdateRequest;
import jakarta.servlet.http.HttpSession;

public interface ProfileService {
    
    Employee getCurrentEmployee(HttpSession session);

    void updateProfile(ProfileUpdateRequest request, HttpSession session);

    void changePassword(ChangePasswordRequest request, HttpSession session);
}
