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
        String method = request.getMethod();

        // 1. Chỉ OWNER: /branch
        if (uri.startsWith("/branch") && !loggedInUser.hasRole("OWNER")) {
            response.sendRedirect(contextPath + "/error/403");
            return false;
        }

        // 2. OWNER, BRANCH_MANAGER: /employee, /reports, /vouchers
        if ((uri.startsWith("/employee") || uri.startsWith("/reports") || uri.startsWith("/vouchers"))
                && !loggedInUser.hasAnyRole("OWNER", "BRANCH_MANAGER")) {
            response.sendRedirect(contextPath + "/error/403");
            return false;
        }

        // 3. OWNER, BRANCH_MANAGER, WAREHOUSE_STAFF: /inventory, /imports, /supplier
        if ((uri.startsWith("/inventory") || uri.startsWith("/imports") || uri.startsWith("/supplier"))
                && !loggedInUser.hasAnyRole("OWNER", "BRANCH_MANAGER", "WAREHOUSE_STAFF")) {
            response.sendRedirect(contextPath + "/error/403");
            return false;
        }

        // 4. OWNER, BRANCH_MANAGER, SALE_STAFF: /pos, /orders, /return, /customers, /cashbook
        if ((uri.startsWith("/pos") || uri.startsWith("/orders") || uri.startsWith("/return") 
                || uri.startsWith("/customers") || uri.startsWith("/cashbook"))
                && !loggedInUser.hasAnyRole("OWNER", "BRANCH_MANAGER", "SALE_STAFF")) {
            response.sendRedirect(contextPath + "/error/403");
            return false;
        }

        // 5. Product Management: /product, /category, /brand, /unit
        // GET (View): Mọi role đều được tra cứu
        // Chặn hiển thị form (GET /form, /popup-form) và cấm thao tác ghi (POST) đối với tất cả role trừ OWNER
        if (uri.startsWith("/product") || uri.startsWith("/category") 
                || uri.startsWith("/brand") || uri.startsWith("/unit")) {
            
            boolean isWriteMethod = "POST".equalsIgnoreCase(method);
            boolean isFormUrl = uri.contains("/form") || uri.contains("/popup-form") || uri.contains("/delete");
            
            if ((isWriteMethod || isFormUrl) && !loggedInUser.hasRole("OWNER")) {
                response.sendRedirect(contextPath + "/error/403");
                return false;
            }
        }

        // Các URL khác (ví dụ: /, /profile) cho phép tất cả các tài khoản đã đăng nhập
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
