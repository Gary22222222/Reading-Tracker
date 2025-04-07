package com.group20.dailyreadingtracker.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

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
                    "/auth",
                    "/auth/**",
                    "/uploads/**",
                    "/verify-email",
                    "/verify-email/**",
                    "/verify-email**",
                    "/resend-verification",
                    "/resend-verification/**",
                    "/verify-pending",
                    "/verify-pending/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/forgot-password",
                    "/forgot-password/**",
                    "/reset-password/**",
                    "/reset-password",
                    "/logout" 
                ).permitAll()
                
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth")
                .failureHandler(authenticationFailureHandler())
                .loginProcessingUrl("/login") 
                .defaultSuccessUrl("/home", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/auth?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            
            .exceptionHandling(exceptions -> exceptions
            .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/auth"))
        );

        return http.build();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            String email = request.getParameter("username");
            String redirectUrl = "/auth?error=true";
            
            if (exception instanceof DisabledException) {
                redirectUrl = "/verify-pending?email=" + email;
            }
            
            response.sendRedirect(redirectUrl);
        };
    }
}