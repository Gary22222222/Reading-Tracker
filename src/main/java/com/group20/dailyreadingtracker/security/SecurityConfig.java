package com.group20.dailyreadingtracker.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.group20.dailyreadingtracker.user.User;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/register",
                    "/register/**",
                    "/login",
                    "/login/**",
                    "/uploads/**",
                    "/verify-email",
                    "/verify-email/**",
                    "/verify-email**",
                    "/resend-verification",
                    "/verify-pending",
                    "/css/**",
                    "/js/**",
                    "/forgot-password",
                    "/reset-password**",
                    "/logout" 
                ).permitAll()
                
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(authenticationSuccessHandler())
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            
            .exceptionHandling(exceptions -> exceptions
            .accessDeniedHandler((request, response, accessDeniedException) -> {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.getPrincipal() instanceof User) {
                    User user = (User) auth.getPrincipal();
                    if (!user.getIsEnabled()) {
                        response.sendRedirect("/verify-pending?email=" + user.getEmail());
                        return;
                    }
                }
                response.sendRedirect("/access-denied");
            })
        );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            User user = (User) authentication.getPrincipal();
            if (!user.getIsEnabled()) {
                response.sendRedirect("/verify-pending?email=" + user.getEmail());
            } else {
                response.sendRedirect("/home");
            }
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && !((User) auth.getPrincipal()).getIsEnabled()) {
                response.sendRedirect("/verify-pending?email=" + ((User) auth.getPrincipal()).getEmail());
            } else {
                response.sendRedirect("/access-denied");
            }
        };
    }
}