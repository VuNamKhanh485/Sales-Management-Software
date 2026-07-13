package com.g4fpt.sms.auth.security;

import com.g4fpt.sms.auth.dto.SessionUser;
import com.g4fpt.sms.auth.util.SessionConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String uri = request.getServletPath();
        String contextPath = request.getContextPath();

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(SessionConstants.LOGGED_IN_USER) == null) {
            response.sendRedirect(contextPath + "/auth/login");
            return false;
        }

        SessionUser loggedInUser = (SessionUser) session.getAttribute(SessionConstants.LOGGED_IN_USER);

        if (!loggedInUser.hasRole("OWNER")) {
            response.sendRedirect(contextPath + "/error/403");
            return false;
        }

        if (uri.startsWith("/inventory")
                && !loggedInUser.hasAnyRole("OWNER", "BRANCH_MANAGER", "WAREHOUSE_STAFF")) {
            response.sendRedirect("/error/403");
            return false;
        }

        if (uri.startsWith("/pos")
                && !loggedInUser.hasAnyRole("OWNER", "BRANCH_MANAGER", "SALE_STAFF")) {
            response.sendRedirect("/error/403");
            return false;
        }

        if (uri.startsWith("/branch") && !loggedInUser.hasRole("OWNER")) {
            response.sendRedirect(contextPath + "/error/403");
            return false;
        }

        if (uri.startsWith("/employee")
                && !loggedInUser.hasAnyRole("OWNER", "BRANCH_MANAGER")) {
            response.sendRedirect(contextPath + "/error/403");
            return false;
        }

        return true;
    }
}
