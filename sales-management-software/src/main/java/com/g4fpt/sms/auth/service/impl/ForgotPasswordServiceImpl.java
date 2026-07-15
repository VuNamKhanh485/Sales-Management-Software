package com.g4fpt.sms.auth.service.impl;

import com.g4fpt.sms.auth.dto.OtpValidationResult;
import com.g4fpt.sms.auth.service.EmailService;
import com.g4fpt.sms.auth.service.ForgotPasswordService;
import com.g4fpt.sms.auth.service.OtpService;
import com.g4fpt.sms.employee.entity.Employee;
import com.g4fpt.sms.employee.repository.EmployeeRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private static final String ACTIVE_STATUS = "ACTIVE";

    private final EmployeeRepository employeeRepository;
    private final EmailService emailService;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;

    public ForgotPasswordServiceImpl(
            EmployeeRepository employeeRepository,
            EmailService emailService,
            OtpService otpService,
            PasswordEncoder passwordEncoder
    ) {
        this.employeeRepository = employeeRepository;
        this.emailService = emailService;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RequestOtpResult requestOtp(
            String email,
            HttpSession session
    ) {
        String normalizedEmail = normalizeEmail(email);

        Optional<Employee> employeeOptional =
                employeeRepository.findByEmailIgnoreCase(normalizedEmail);

        if (employeeOptional.isEmpty()) {
            return RequestOtpResult.failure(
                    RequestOtpStatus.EMAIL_NOT_FOUND
            );
        }

        Employee employee = employeeOptional.get();

        if (!isEmployeeActive(employee)) {
            return RequestOtpResult.failure(
                    RequestOtpStatus.ACCOUNT_INACTIVE
            );
        }

        if (!otpService.canResendOtp(session)) {
            return RequestOtpResult.cooldown(
                    otpService.getRemainingResendSeconds(session)
            );
        }

        String otp = otpService.generateOtp();

        /*
         * Lưu OTP trước khi gửi mail để thời gian hết hạn được tính từ lúc
         * bắt đầu gửi. Nếu gửi thất bại, toàn bộ session của luồng này
         * sẽ được xóa ngay.
         */
        otpService.storeOtp(
                session,
                normalizedEmail,
                otp
        );

        try {
            emailService.sendOtpEmail(
                    normalizedEmail,
                    otp
            );
        } catch (RuntimeException exception) {
            otpService.clearForgotPasswordSession(session);

            return RequestOtpResult.failure(
                    RequestOtpStatus.EMAIL_SEND_FAILED
            );
        }

        return RequestOtpResult.success();
    }

    @Override
    public OtpValidationResult verifyOtp(
            String inputOtp,
            HttpSession session
    ) {
        if (otpService.getForgotPasswordEmail(session) == null) {
            return OtpValidationResult.NOT_FOUND;
        }

        OtpValidationResult result =
                otpService.validateOtp(session, inputOtp);

        if (result == OtpValidationResult.VALID) {
            otpService.markVerified(session);
        }

        return result;
    }

    @Override
    public boolean canAccessVerifyOtp(HttpSession session) {
        return otpService.getForgotPasswordEmail(session) != null;
    }

    @Override
    public boolean canAccessResetPassword(HttpSession session) {
        return otpService.getForgotPasswordEmail(session) != null
                && otpService.isVerified(session);
    }

    @Override
    @Transactional
    public ResetPasswordStatus resetPassword(
            String newPassword,
            HttpSession session
    ) {
        String email = otpService.getForgotPasswordEmail(session);

        if (email == null || !otpService.isVerified(session)) {
            return ResetPasswordStatus.SESSION_INVALID;
        }

        Optional<Employee> employeeOptional =
                employeeRepository.findByEmailIgnoreCase(email);

        if (employeeOptional.isEmpty()) {
            otpService.clearForgotPasswordSession(session);
            return ResetPasswordStatus.EMPLOYEE_NOT_FOUND;
        }

        Employee employee = employeeOptional.get();

        if (!isEmployeeActive(employee)) {
            otpService.clearForgotPasswordSession(session);
            return ResetPasswordStatus.ACCOUNT_INACTIVE;
        }

        String encodedPassword =
                passwordEncoder.encode(newPassword);

        employee.setPasswordHash(encodedPassword);
        employeeRepository.save(employee);

        otpService.clearForgotPasswordSession(session);

        return ResetPasswordStatus.SUCCESS;
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return "";
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }

    private boolean isEmployeeActive(Employee employee) {
        Object workStatus = employee.getWorkStatus();

        if (workStatus == null) {
            return false;
        }

        return ACTIVE_STATUS.equalsIgnoreCase(
                String.valueOf(workStatus)
        );
    }
}