package com.g4fpt.sms.auth.controller;


import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model
    ) {
        if (error != null) {
            model.addAttribute("loginError", "Email hoặc mật khẩu không đúng, hoặc tài khoản đã bị khóa.");
        }

        if (logout != null) {
            model.addAttribute("loginSuccess", "Đăng xuất thành công.");
        }
        return "auth/login";
    }
    @GetMapping("/home")
    public String getHome(){
        return "home";
    }

}
