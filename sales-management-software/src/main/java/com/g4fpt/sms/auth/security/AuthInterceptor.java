package com.g4fpt.sms.auth.security;

import com.g4fpt.sms.auth.dto.SessionUser;
import com.g4fpt.sms.auth.util.SessionConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String uri = request.getServletPath();
        String contextPath = request.getContextPath();

        HttpSession session = request.getSession(false);

        // Kiểm tra session — nếu chưa đăng nhập thì redirect về trang login
        if (session == null || session.getAttribute(SessionConstants.LOGGED_IN_USER) == null) {
            response.sendRedirect(contextPath + "/auth/login");
            return false;
        }

        SessionUser loggedInUser = (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);

        // /inventory/** và /imports/** — chỉ OWNER, BRANCH_MANAGER, WAREHOUSE_STAFF được phép
        if ((uri.startsWith("/inventory") || uri.startsWith("/imports"))
                && !loggedInUser.hasAnyRole("OWNER", "BRANCH_MANAGER", "WAREHOUSE_STAFF")) {
            response.sendRedirect(contextPath + "/error/403");
            return false;
        }

        // /pos/** — chỉ OWNER, BRANCH_MANAGER, SALE_STAFF được phép
        if (uri.startsWith("/pos")
                && !loggedInUser.hasAnyRole("OWNER", "BRANCH_MANAGER", "SALE_STAFF")) {
            response.sendRedirect(contextPath + "/error/403");
            return false;
        }

        // /branch/** — chỉ OWNER được phép
        if (uri.startsWith("/branch") && !loggedInUser.hasRole("OWNER")) {
            response.sendRedirect(contextPath + "/error/403");
            return false;
        }

        // /employee/** — chỉ OWNER và BRANCH_MANAGER được phép
        if (uri.startsWith("/employee")
                && !loggedInUser.hasAnyRole("OWNER", "BRANCH_MANAGER")) {
            response.sendRedirect(contextPath + "/error/403");
            return false;
        }

        return true;
    }

    /**
     * Sau khi request được xử lý thành công, tự động đưa sessionUser vào Model
     * để tất cả Thymeleaf templates có thể dùng ${sessionUser} mà không cần
     * controller nào phải gọi model.addAttribute("sessionUser", ...) thủ công.
     */
    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                SessionUser sessionUser = (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);
                if (sessionUser != null) {
                    modelAndView.addObject("sessionUser", sessionUser);
                }
            }
        }
    }
}
