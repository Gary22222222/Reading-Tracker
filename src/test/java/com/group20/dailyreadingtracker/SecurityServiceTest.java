package com.group20.dailyreadingtracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.group20.dailyreadingtracker.security.SecurityService;

@ExtendWith(MockitoExtension.class)
public class SecurityServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private UserDetailsService userDetailsService;
    
    @InjectMocks
    private SecurityService securityService;
    
    private UserDetails userDetails;
    private UsernamePasswordAuthenticationToken authentication;

    @BeforeEach
    void setup() {
        userDetails = org.springframework.security.core.userdetails.User
            .withUsername("testuser")
            .password("password")
            .roles("USER")
            .build();
            
        authentication = new UsernamePasswordAuthenticationToken(
            userDetails, "password", userDetails.getAuthorities());
    }

    @Test
    void testIsAuthenticated() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        assertTrue(securityService.isAuthenticated());
    }

    @Test
    void testIsAuthenticatedFailure() {
        SecurityContextHolder.clearContext();
        
        assertFalse(securityService.isAuthenticated());
    }

    @Test
    void testAutoLogin() {
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        
        securityService.autoLogin("testuser", "password");
        
        Authentication result = SecurityContextHolder.getContext().getAuthentication();
        assertEquals("testuser", result.getName());
    }

}
