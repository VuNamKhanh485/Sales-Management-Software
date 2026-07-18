package com.g4fpt.sms.profile.service;

import com.g4fpt.sms.auth.dto.SessionUser;
import com.g4fpt.sms.auth.util.SessionConstants;
import com.g4fpt.sms.employee.entity.Employee;
import com.g4fpt.sms.employee.repository.EmployeeRepository;
import com.g4fpt.sms.profile.dto.ChangePasswordRequest;
import com.g4fpt.sms.profile.dto.ProfileUpdateRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Employee getCurrentEmployee(HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);
        if (sessionUser == null) {
            throw new RuntimeException("Chưa đăng nhập");
        }
        return employeeRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin nhân viên"));
    }

    @Override
    @Transactional
    public void updateProfile(ProfileUpdateRequest request, HttpSession session) {
        Employee employee = getCurrentEmployee(session);

        // Update employee info
        employee.setFullName(request.getFullName());
        employee.setPhone(request.getPhone());
        employee.setGender(request.getGender());
        employee.setDob(request.getDob());
        employee.setAddress(request.getAddress());

        employeeRepository.save(employee);

        // Update session user
        SessionUser sessionUser = (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);
        if (sessionUser != null) {
            sessionUser.setFullName(employee.getFullName());
            session.setAttribute(SessionConstants.LOGGED_IN_USER, sessionUser);
        }
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request, HttpSession session) {
        Employee employee = getCurrentEmployee(session);

        if (!passwordEncoder.matches(request.getCurrentPassword(), employee.getPasswordHash())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng");
        }

        if (passwordEncoder.matches(request.getNewPassword(), employee.getPasswordHash())) {
            throw new IllegalArgumentException("Mật khẩu mới không được giống mật khẩu hiện tại");
        }
        
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Xác nhận mật khẩu không khớp");
        }

        employee.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        employeeRepository.save(employee);
    }
}
