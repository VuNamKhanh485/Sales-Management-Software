package com.g4fpt.sms.auth.service;

public interface EmailService {

    void sendOtpEmail(String toEmail, String otp);
}
