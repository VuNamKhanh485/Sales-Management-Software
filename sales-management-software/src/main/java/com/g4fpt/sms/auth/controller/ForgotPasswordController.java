package com.g4fpt.sms.auth.controller;

import com.g4fpt.sms.auth.dto.ForgotPasswordForm;
import com.g4fpt.sms.auth.dto.OtpValidationResult;
import com.g4fpt.sms.auth.dto.ResetPasswordForm;
import com.g4fpt.sms.auth.dto.VerifyOtpForm;
import com.g4fpt.sms.auth.service.ForgotPasswordService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@RequestMapping("/auth")
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    public ForgotPasswordController(
            ForgotPasswordService forgotPasswordService
    ) {
        this.forgotPasswordService = forgotPasswordService;
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        if (!model.containsAttribute("forgotPasswordForm")) {
            model.addAttribute(
                    "forgotPasswordForm",
                    new ForgotPasswordForm()
            );
        }

        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @Valid
            @ModelAttribute("forgotPasswordForm")
            ForgotPasswordForm form,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/forgot-password";
        }

        ForgotPasswordService.RequestOtpResult result =
                forgotPasswordService.requestOtp(
                        form.getEmail(),
                        session
                );

        switch (result.status()) {
            case SUCCESS -> {
                redirectAttributes.addFlashAttribute(
                        "infoMessage",
                        "Mã OTP đã được gửi đến email của bạn."
                );

                return "redirect:/auth/verify-otp";
            }

            case EMAIL_NOT_FOUND -> bindingResult.rejectValue(
                    "email",
                    "email.notFound",
                    "Email không tồn tại."
            );

            case ACCOUNT_INACTIVE -> bindingResult.rejectValue(
                    "email",
                    "account.inactive",
                    "Tài khoản không hoạt động."
            );

            case RESEND_COOLDOWN -> bindingResult.rejectValue(
                    "email",
                    "otp.cooldown",
                    "Vui lòng chờ "
                            + result.remainingSeconds()
                            + " giây trước khi gửi lại OTP."
            );

            case EMAIL_SEND_FAILED -> bindingResult.reject(
                    "email.sendFailed",
                    "Không thể gửi email OTP. Vui lòng thử lại sau."
            );
        }

        return "auth/forgot-password";
    }

    @GetMapping("/verify-otp")
    public String showVerifyOtpForm(
            Model model,
            HttpSession session
    ) {
        if (!forgotPasswordService.canAccessVerifyOtp(session)) {
            return "redirect:/auth/forgot-password";
        }

        if (!model.containsAttribute("verifyOtpForm")) {
            model.addAttribute(
                    "verifyOtpForm",
                    new VerifyOtpForm()
            );
        }

        return "auth/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String processVerifyOtp(
            @Valid
            @ModelAttribute("verifyOtpForm")
            VerifyOtpForm form,
            BindingResult bindingResult,
            HttpSession session
    ) {
        if (!forgotPasswordService.canAccessVerifyOtp(session)) {
            return "redirect:/auth/forgot-password";
        }

        if (bindingResult.hasErrors()) {
            return "auth/verify-otp";
        }

        OtpValidationResult result =
                forgotPasswordService.verifyOtp(
                        form.getOtp(),
                        session
                );

        switch (result) {
            case VALID -> {
                return "redirect:/auth/reset-password";
            }

            case INVALID -> bindingResult.rejectValue(
                    "otp",
                    "otp.invalid",
                    "Mã OTP không chính xác."
            );

            case EXPIRED -> bindingResult.rejectValue(
                    "otp",
                    "otp.expired",
                    "Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới."
            );

            case NOT_FOUND -> bindingResult.rejectValue(
                    "otp",
                    "otp.notFound",
                    "Không tìm thấy mã OTP. Vui lòng yêu cầu mã mới."
            );

            case MAX_ATTEMPTS_EXCEEDED -> bindingResult.rejectValue(
                    "otp",
                    "otp.maxAttempts",
                    "Bạn đã nhập sai OTP quá số lần cho phép. "
                            + "Vui lòng yêu cầu mã mới."
            );
        }

        return "auth/verify-otp";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(
            Model model,
            HttpSession session
    ) {
        if (!forgotPasswordService.canAccessResetPassword(session)) {
            return "redirect:/auth/forgot-password";
        }

        if (!model.containsAttribute("resetPasswordForm")) {
            model.addAttribute(
                    "resetPasswordForm",
                    new ResetPasswordForm()
            );
        }

        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(
            @Valid
            @ModelAttribute("resetPasswordForm")
            ResetPasswordForm form,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        if (!forgotPasswordService.canAccessResetPassword(session)) {
            return "redirect:/auth/forgot-password";
        }

        validatePasswordConfirmation(form, bindingResult);

        if (bindingResult.hasErrors()) {
            return "auth/reset-password";
        }

        ForgotPasswordService.ResetPasswordStatus result =
                forgotPasswordService.resetPassword(
                        form.getNewPassword(),
                        session
                );

        switch (result) {
            case SUCCESS -> {
                redirectAttributes.addFlashAttribute(
                        "successMessage",
                        "Đổi mật khẩu thành công. "
                                + "Vui lòng đăng nhập bằng mật khẩu mới."
                );

                return "redirect:/auth/login";
            }

            case SESSION_INVALID -> {
                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "Phiên đặt lại mật khẩu không hợp lệ hoặc đã hết hạn."
                );

                return "redirect:/auth/forgot-password";
            }

            case EMPLOYEE_NOT_FOUND -> {
                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "Tài khoản không còn tồn tại. "
                                + "Vui lòng liên hệ quản trị viên."
                );

                return "redirect:/auth/forgot-password";
            }

            case ACCOUNT_INACTIVE -> {
                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "Tài khoản không hoạt động."
                );

                return "redirect:/auth/forgot-password";
            }

            default -> {
                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "Không thể đổi mật khẩu. Vui lòng thử lại."
                );

                return "redirect:/auth/forgot-password";
            }
        }
    }

    private void validatePasswordConfirmation(
            ResetPasswordForm form,
            BindingResult bindingResult
    ) {
        if (form.getNewPassword() == null
                || form.getConfirmPassword() == null) {
            return;
        }

        if (!Objects.equals(
                form.getNewPassword(),
                form.getConfirmPassword()
        )) {
            bindingResult.rejectValue(
                    "confirmPassword",
                    "password.mismatch",
                    "Xác nhận mật khẩu không khớp với mật khẩu mới."
            );
        }
    }
}