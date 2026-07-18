package com.g4fpt.sms.profile.controller;

import com.g4fpt.sms.employee.entity.Employee;
import com.g4fpt.sms.profile.dto.ChangePasswordRequest;
import com.g4fpt.sms.profile.dto.ProfileUpdateRequest;
import com.g4fpt.sms.profile.service.ProfileService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public String showProfile(Model model, HttpSession session) {
        Employee currentEmployee = profileService.getCurrentEmployee(session);
        model.addAttribute("employee", currentEmployee);
        return "profile/profile";
    }

    @GetMapping("/edit")
    public String showProfileEdit(Model model, HttpSession session) {
        if (!model.containsAttribute("profileUpdateRequest")) {
            Employee currentEmployee = profileService.getCurrentEmployee(session);
            ProfileUpdateRequest request = ProfileUpdateRequest.builder()
                    .fullName(currentEmployee.getFullName())
                    .phone(currentEmployee.getPhone())
                    .gender(currentEmployee.getGender())
                    .dob(currentEmployee.getDob())
                    .address(currentEmployee.getAddress())
                    .build();
            model.addAttribute("profileUpdateRequest", request);
        }
        return "profile/profile-edit";
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("profileUpdateRequest") ProfileUpdateRequest request,
                                BindingResult result,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "profile/profile-edit";
        }

        try {
            profileService.updateProfile(request, session);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin cá nhân thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
        }

        return "redirect:/profile";
    }

    @GetMapping("/account")
    public String showAccountPage(Model model, HttpSession session) {
        if (!model.containsAttribute("passwordForm")) {
            model.addAttribute("passwordForm", new ChangePasswordRequest());
        }
        Employee currentEmployee = profileService.getCurrentEmployee(session);
        return "profile/account";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("passwordForm") ChangePasswordRequest request,
                                 BindingResult result,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (result.hasErrors()) {
            Employee currentEmployee = profileService.getCurrentEmployee(session);
            return "profile/account";
        }

        try {
            profileService.changePassword(request, session);
            redirectAttributes.addFlashAttribute("passwordSuccess", "Đổi mật khẩu thành công");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Mật khẩu hiện tại")) {
                result.rejectValue("currentPassword", "error.currentPassword", e.getMessage());
            } else if (e.getMessage().contains("Mật khẩu mới không được giống")) {
                result.rejectValue("newPassword", "error.newPassword", e.getMessage());
            } else if (e.getMessage().contains("Xác nhận mật khẩu không khớp")) {
                result.rejectValue("confirmPassword", "error.confirmPassword", e.getMessage());
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/profile/account";
            }
            Employee currentEmployee = profileService.getCurrentEmployee(session);
            return "profile/account";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
        }

        return "redirect:/profile/account";
    }
}
