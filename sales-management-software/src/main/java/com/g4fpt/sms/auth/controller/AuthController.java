package com.g4fpt.sms.auth.controller;

import com.g4fpt.sms.auth.dto.LoginRequest;
import com.g4fpt.sms.auth.service.AuthService;
import com.g4fpt.sms.auth.service.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest loginRequest, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            CustomUserDetails userDetails = authService.login(loginRequest);
            session.setAttribute("loggedInUser", userDetails);
            redirectAttributes.addFlashAttribute("loginSuccess", "Đăng nhập thành công.");
            return "redirect:/login";
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("loginError", "Email hoặc mật khẩu không đúng.");
            return "redirect:/login";
        }
    }


}
