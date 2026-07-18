package com.g4fpt.sms.auth.config;

import com.g4fpt.sms.auth.security.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class AuthWebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public AuthWebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve return images from project-root/return-image/ folder
        Path returnImagePath = Paths.get(System.getProperty("user.dir"), "return-image");
        String returnImageLocation = returnImagePath.toUri().toString();

        registry.addResourceHandler("/return-image/**")
                .addResourceLocations(returnImageLocation);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/login",
                        "/auth/logout",
                        "/auth/forgot-password",
                        "/auth/verify-otp",
                        "/auth/reset-password",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/webjars/**",
                        "/favicon.ico",
                        "/error",
                        "/return-image/**"
                );
    }
}
