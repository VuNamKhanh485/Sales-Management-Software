package com.g4fpt.sms.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class VerifyOtpForm {

    @NotBlank(message = "Mã OTP không được để trống")
    @Pattern(
            regexp = "^\\d{6}$",
            message = "Mã OTP phải gồm đúng 6 chữ số"
    )
    private String otp;

    public VerifyOtpForm() {
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp == null ? null : otp.trim();
    }
}
