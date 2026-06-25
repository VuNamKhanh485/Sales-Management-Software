package com.g4fpt.sms.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;


@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String redirect = null;
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authorityName : authorities) {
            if (authorityName.getAuthority().equals("ROLE_OWNER")
                    || authorityName.getAuthority().equals("ROLE_MANAGER")) {
                redirect = "/branch";
            } else if (authorityName.getAuthority().equals("ROLE_SALE_STAFF")) {
                redirect = "/pos";
            }

        }
        if (redirect == null) {
            throw new IllegalStateException("User role is unavailable");
        }
        response.sendRedirect(request.getContextPath() + redirect);
    }
}
