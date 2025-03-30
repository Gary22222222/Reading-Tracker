package com.group20.dailyreadingtracker;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.group20.dailyreadingtracker.auth.EmailService;
import com.group20.dailyreadingtracker.auth.PasswordResetTokenRepository;

@Configuration
public class TestConfig {

    @Bean
    public EmailService emailService(){
        return Mockito.mock(EmailService.class);
    }

    @Bean
    public PasswordResetTokenRepository passwordResetTokenRepository(){
        return Mockito.mock(PasswordResetTokenRepository.class);
    }
    
}
