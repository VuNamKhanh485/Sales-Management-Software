package com.g4fpt.sms.auth.controller;


import com.g4fpt.sms.auth.dto.LoginForm;
import com.g4fpt.sms.auth.dto.SessionUser;
import com.g4fpt.sms.auth.service.AuthService;
import com.g4fpt.sms.auth.util.SessionConstants;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "auth/login";
    }

    @PostMapping("/login")
    public String processLogin(@Valid @ModelAttribute("loginForm") LoginForm form,
                               BindingResult result,
                               HttpSession session,
                               Model model) {
        if (result.hasErrors()) {
            return "auth/login";
        }

        Optional<SessionUser> optionalUser = authService.authenticate(form.getEmail(), form.getPassword());

        if (optionalUser.isEmpty()) {
            model.addAttribute("loginError", "Email hoặc mật khẩu không đúng, hoặc tài khoản đã bị khóa");
            return "auth/login";
        }

        SessionUser sessionUser = optionalUser.get();
        session.setAttribute(SessionConstants.LOGGED_IN_USER, sessionUser);

        if (sessionUser.hasRole("OWNER")) {
            return "redirect:/dashboard";
        }

        if (sessionUser.hasRole("BRANCH_MANAGER")) {
            return "redirect:/inventory";
        }

        if (sessionUser.hasRole("WAREHOUSE_STAFF")) {
            return "redirect:/inventory";
        }

        if (sessionUser.hasRole("SALE_STAFF")) {
            return "redirect:/pos";
        }

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }
}
