package com.group20.dailyreadingtracker;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.group20.dailyreadingtracker.auth.AuthService;
import com.group20.dailyreadingtracker.auth.FileStorageService;
import com.group20.dailyreadingtracker.auth.VerificationTokenService;
import com.group20.dailyreadingtracker.role.Role;
import com.group20.dailyreadingtracker.role.RoleRepository;
import com.group20.dailyreadingtracker.user.User;
import com.group20.dailyreadingtracker.user.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RoleRepository roleRepository;
    
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    
    @Mock
    private FileStorageService fileStorageService;
    
    @Mock
    private VerificationTokenService verificationTokenService;
    
    @InjectMocks
    private AuthService authService;

    @Test
    public void testRegisterNewUser() throws IOException {
        User user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setPassword("password");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER"))
            .thenReturn(Optional.of(new Role("ROLE_USER")));

        authService.register(user, null, mock(HttpServletRequest.class));

        verify(userRepository).save(any(User.class));
        verify(verificationTokenService).createVerificationForRegisteredUser(anyString(), any());
    }

    @Test
    public void testRegisterExistingUser() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        User user = new User();
        user.setEmail("existing@example.com");
        
        assertThrows(IllegalArgumentException.class, 
            () -> authService.register(user, null, null));
    }

    @Test
    public void testFindByEmail() {
        User expectedUser = new User();
        expectedUser.setEmail("found@example.com");
        
        when(userRepository.findByEmail("found@example.com"))
            .thenReturn(Optional.of(expectedUser));

        Optional<User> result = authService.findByEmail("found@example.com");
        
        assertTrue(result.isPresent());
        assertEquals("found@example.com", result.get().getEmail());
    }
}