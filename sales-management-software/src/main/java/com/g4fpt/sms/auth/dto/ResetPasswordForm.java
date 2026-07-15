package com.g4fpt.sms.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordForm {

    @NotBlank(message = "Mật khẩu mới không được để trống.")
    @Size(
            min = 8,
            max = 72,
            message = "Mật khẩu mới phải có từ 8 đến 72 ký tự."
    )
    private String newPassword;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống.")
    private String confirmPassword;

    public ResetPasswordForm() {
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}