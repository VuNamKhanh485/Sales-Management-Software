package com.g4fpt.sms.auth.service;

import com.g4fpt.sms.auth.dto.OtpValidationResult;
import jakarta.servlet.http.HttpSession;

public interface OtpService {

    String generateOtp();

    void storeOtp(
            HttpSession session,
            String email,
            String otp
    );

    boolean canResendOtp(HttpSession session);

    long getRemainingResendSeconds(HttpSession session);

    OtpValidationResult validateOtp(
            HttpSession session,
            String inputOtp
    );

    boolean isVerified(HttpSession session);

    void markVerified(HttpSession session);

    String getForgotPasswordEmail(HttpSession session);

    void clearOtp(HttpSession session);

    void clearForgotPasswordSession(HttpSession session);
}