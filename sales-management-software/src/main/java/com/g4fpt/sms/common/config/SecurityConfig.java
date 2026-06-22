package com.g4fpt.sms.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll()
                        // 1. Quản lý chi nhánh (Branch) dành riêng cho OWNER và ADMIN
                        .requestMatchers("/branch/**").hasAnyRole("OWNER", "ADMIN")
                        // 2. Dashboard và Voucher dành cho OWNER, ADMIN và BRANCH_MANAGER
                        .requestMatchers("/dashboard/**", "/").hasAnyRole("OWNER", "ADMIN", "BRANCH_MANAGER")
                        .requestMatchers("/vouchers/**").hasAnyRole("OWNER", "ADMIN", "BRANCH_MANAGER")
                        // 3. Quản lý sản phẩm dành cho OWNER, ADMIN, BRANCH_MANAGER, và WAREHOUSE_STAFF
                        .requestMatchers("/product/**", "/category/**", "/brand/**", "/unit/**", "/product-unit/**")
                            .hasAnyRole("OWNER", "ADMIN", "BRANCH_MANAGER", "WAREHOUSE_STAFF")
                        // 4. Bán hàng POS và Quản lý khách hàng dành cho OWNER, ADMIN, BRANCH_MANAGER, và SALE_STAFF
                        .requestMatchers("/pos/**").hasAnyRole("OWNER", "ADMIN", "BRANCH_MANAGER", "SALE_STAFF")
                        .requestMatchers("/customers/**").hasAnyRole("OWNER", "ADMIN", "BRANCH_MANAGER", "SALE_STAFF")
                        // Yêu cầu đăng nhập đối với tất cả các request khác
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/login?error=true")
                        .permitAll()

                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .clearAuthentication(true)
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
