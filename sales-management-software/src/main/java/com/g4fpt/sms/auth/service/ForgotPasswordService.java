package com.g4fpt.sms.auth.service;

import com.g4fpt.sms.auth.dto.OtpValidationResult;
import jakarta.servlet.http.HttpSession;

public interface ForgotPasswordService {

    RequestOtpResult requestOtp(
            String email,
            HttpSession session
    );

    OtpValidationResult verifyOtp(
            String inputOtp,
            HttpSession session
    );

    boolean canAccessVerifyOtp(HttpSession session);

    boolean canAccessResetPassword(HttpSession session);

    ResetPasswordStatus resetPassword(
            String newPassword,
            HttpSession session
    );

    enum RequestOtpStatus {
        SUCCESS,
        EMAIL_NOT_FOUND,
        ACCOUNT_INACTIVE,
        RESEND_COOLDOWN,
        EMAIL_SEND_FAILED
    }

    enum ResetPasswordStatus {
        SUCCESS,
        SESSION_INVALID,
        EMPLOYEE_NOT_FOUND,
        ACCOUNT_INACTIVE
    }

    record RequestOtpResult(
            RequestOtpStatus status,
            long remainingSeconds
    ) {

        public static RequestOtpResult success() {
            return new RequestOtpResult(
                    RequestOtpStatus.SUCCESS,
                    0L
            );
        }

        public static RequestOtpResult failure(
                RequestOtpStatus status
        ) {
            return new RequestOtpResult(status, 0L);
        }

        public static RequestOtpResult cooldown(
                long remainingSeconds
        ) {
            return new RequestOtpResult(
                    RequestOtpStatus.RESEND_COOLDOWN,
                    Math.max(remainingSeconds, 0L)
            );
        }
    }
}