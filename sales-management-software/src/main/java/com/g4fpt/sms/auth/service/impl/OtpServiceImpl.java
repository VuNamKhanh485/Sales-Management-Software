package com.g4fpt.sms.auth.service.impl;

import com.g4fpt.sms.auth.dto.OtpValidationResult;
import com.g4fpt.sms.auth.service.OtpService;
import com.g4fpt.sms.auth.util.SessionConstants;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Locale;

@Service
public class OtpServiceImpl implements OtpService {

    private static final long OTP_VALIDITY_MILLISECONDS = 5L * 60L * 1000L;

    private static final long OTP_RESEND_COOLDOWN_MILLISECONDS = 60L * 1000L;

    private static final int MAX_OTP_ATTEMPTS = 5;

    private static final int OTP_BOUND = 1_000_000;

    private final SecureRandom secureRandom;

    public OtpServiceImpl(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }

    @Override
    public String generateOtp() {
        int generatedNumber = secureRandom.nextInt(OTP_BOUND);

        return String.format(
                Locale.ROOT,
                "%06d",
                generatedNumber);
    }

    @Override
    public void storeOtp(HttpSession session, String email, String otp) {
        long currentTime = System.currentTimeMillis();
        Long expiresAt = currentTime + OTP_VALIDITY_MILLISECONDS;

        session.setAttribute(SessionConstants.FORGOT_PASSWORD_EMAIL, email);

        session.setAttribute(SessionConstants.FORGOT_PASSWORD_OTP, otp);

        session.setAttribute(SessionConstants.FORGOT_PASSWORD_OTP_EXPIRES_AT, expiresAt);

        session.setAttribute(SessionConstants.FORGOT_PASSWORD_VERIFIED, Boolean.FALSE);

        session.setAttribute(SessionConstants.OTP_LAST_SENT_AT, currentTime);

        session.setAttribute(SessionConstants.FORGOT_PASSWORD_OTP_ATTEMPTS, 0);
    }

    @Override
    public boolean canResendOtp(HttpSession session) {
        Object lastSentAttribute = session.getAttribute(SessionConstants.OTP_LAST_SENT_AT);

        if (!(lastSentAttribute instanceof Number lastSentNumber)) {
            return true;
        }

        long lastSentAt = lastSentNumber.longValue();
        long elapsedTime = System.currentTimeMillis() - lastSentAt;

        return elapsedTime >= OTP_VALIDITY_MILLISECONDS;
    }

    @Override
    public long getRemainingResendSeconds(HttpSession session) {
        Object lastSentAttribute = session.getAttribute(
                SessionConstants.OTP_LAST_SENT_AT
        );

        if (!(lastSentAttribute instanceof Number lastSentNumber)) {
            return 0L;
        }

        long lastSentAt = lastSentNumber.longValue();
        long elapsedTime = System.currentTimeMillis() - lastSentAt;
        long remainingMilliseconds =
                OTP_RESEND_COOLDOWN_MILLISECONDS - elapsedTime;

        if (remainingMilliseconds <= 0L) {
            return 0L;
        }

        return (remainingMilliseconds + 999L) / 1000L;
    }

    @Override
    public OtpValidationResult validateOtp(HttpSession session, String inputOtp) {
        Object storedOtpAttribute = session.getAttribute(
                SessionConstants.FORGOT_PASSWORD_OTP
        );

        Object expiresAtAttribute = session.getAttribute(
                SessionConstants.FORGOT_PASSWORD_OTP_EXPIRES_AT
        );

        if (!(storedOtpAttribute instanceof String storedOtp)
                || storedOtp.isBlank()
                || !(expiresAtAttribute instanceof Number expiresAtNumber)) {

            clearOtp(session);
            return OtpValidationResult.NOT_FOUND;
        }

        long expiresAt = expiresAtNumber.longValue();

        if (System.currentTimeMillis() > expiresAt) {
            clearOtp(session);
            return OtpValidationResult.EXPIRED;
        }

        int currentAttempts = getCurrentAttempts(session);

        if (currentAttempts >= MAX_OTP_ATTEMPTS) {
            clearOtp(session);
            return OtpValidationResult.MAX_ATTEMPTS_EXCEEDED;
        }

        String normalizedInputOtp =
                inputOtp == null ? "" : inputOtp.trim();

        if (!storedOtp.equals(normalizedInputOtp)) {
            int newAttempts = currentAttempts + 1;

            if (newAttempts >= MAX_OTP_ATTEMPTS) {
                clearOtp(session);
                return OtpValidationResult.MAX_ATTEMPTS_EXCEEDED;
            }

            session.setAttribute(
                    SessionConstants.FORGOT_PASSWORD_OTP_ATTEMPTS,
                    newAttempts
            );

            return OtpValidationResult.INVALID;
        }

        /*
         * OTP đúng: xóa ngay OTP, hạn sử dụng và số lần thử.
         * Trạng thái verified sẽ được thiết lập riêng qua markVerified().
         */
        session.removeAttribute(
                SessionConstants.FORGOT_PASSWORD_OTP
        );

        session.removeAttribute(
                SessionConstants.FORGOT_PASSWORD_OTP_EXPIRES_AT
        );

        session.removeAttribute(
                SessionConstants.FORGOT_PASSWORD_OTP_ATTEMPTS
        );

        return OtpValidationResult.VALID;
    }

    @Override
    public boolean isVerified(HttpSession session) {
        Object verifiedAttribute = session.getAttribute(
                SessionConstants.FORGOT_PASSWORD_VERIFIED
        );

        return Boolean.TRUE.equals(verifiedAttribute);
    }

    @Override
    public void markVerified(HttpSession session) {
        session.setAttribute(
                SessionConstants.FORGOT_PASSWORD_VERIFIED,
                Boolean.TRUE
        );
    }

    @Override
    public String getForgotPasswordEmail(HttpSession session) {
        Object emailAttribute = session.getAttribute(
                SessionConstants.FORGOT_PASSWORD_EMAIL
        );

        if (!(emailAttribute instanceof String email)) {
            return null;
        }

        String normalizedEmail = email.trim();

        return normalizedEmail.isBlank() ? null : normalizedEmail;
    }

    @Override
    public void clearOtp(HttpSession session) {
        session.removeAttribute(
                SessionConstants.FORGOT_PASSWORD_OTP
        );

        session.removeAttribute(
                SessionConstants.FORGOT_PASSWORD_OTP_EXPIRES_AT
        );

        session.removeAttribute(
                SessionConstants.FORGOT_PASSWORD_OTP_ATTEMPTS
        );

        session.setAttribute(
                SessionConstants.FORGOT_PASSWORD_VERIFIED,
                Boolean.FALSE
        );
    }

    @Override
    public void clearForgotPasswordSession(HttpSession session) {
        session.removeAttribute(
                SessionConstants.FORGOT_PASSWORD_EMAIL
        );

        session.removeAttribute(
                SessionConstants.FORGOT_PASSWORD_OTP
        );

        session.removeAttribute(
                SessionConstants.FORGOT_PASSWORD_OTP_EXPIRES_AT
        );

        session.removeAttribute(
                SessionConstants.FORGOT_PASSWORD_VERIFIED
        );

        session.removeAttribute(
                SessionConstants.OTP_LAST_SENT_AT
        );

        session.removeAttribute(
                SessionConstants.FORGOT_PASSWORD_OTP_ATTEMPTS
        );
    }

    private int getCurrentAttempts(HttpSession session) {
        Object attemptsAttribute = session.getAttribute(
                SessionConstants.FORGOT_PASSWORD_OTP_ATTEMPTS
        );

        if (attemptsAttribute instanceof Number attemptsNumber) {
            return Math.max(attemptsNumber.intValue(), 0);
        }

        return 0;
    }
}
