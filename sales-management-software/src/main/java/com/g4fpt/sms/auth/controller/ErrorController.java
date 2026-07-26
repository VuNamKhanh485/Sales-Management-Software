package com.g4fpt.sms.auth.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @GetMapping("/error/403")
    public String forbidden(Model model) {
        model.addAttribute("errorCode", "403");
        model.addAttribute("errorMessage", "Bạn không có quyền truy cập chức năng này");
        return "error/403";
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            if (statusCode == 404) {
                model.addAttribute("errorCode", "404");
                model.addAttribute("errorMessage", "Trang bạn tìm kiếm không tồn tại hoặc đường dẫn không đúng.");
                return "error/403";
            }
            if (statusCode == 403) {
                model.addAttribute("errorCode", "403");
                model.addAttribute("errorMessage", "Bạn không có quyền truy cập chức năng này.");
                return "error/403";
            }
            if (statusCode == 500) {
                model.addAttribute("errorCode", "500");
                Exception exception = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
                String msg = (exception != null) ? exception.getMessage() : "Unknown 500 error";
                if (exception != null && exception.getCause() != null) msg += " | Cause: " + exception.getCause().getMessage();
                model.addAttribute("errorMessage", "System error: " + msg);
                return "error/403";
            }
        }
        
        model.addAttribute("errorCode", "Lỗi");
        model.addAttribute("errorMessage", "Đã có lỗi không xác định xảy ra.");
        return "error/403";
    }
}
