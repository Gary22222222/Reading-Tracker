package com.group20.dailyreadingtracker;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group20.dailyreadingtracker.user.User;
import com.group20.dailyreadingtracker.user.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryTest {
    
    @Mock
    private UserRepository userRepository;

    @Test
    public void testCreateUser(){
        User user = new User();
        user.setEmail("mamonovasofia@gmail.com");
        user.setPassword("Iamnottellingyou8");
        user.setUsername("slooonya");
        
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        User savedUser = userRepository.save(user);
        
        assertNotNull(savedUser);
        assertEquals("mamonovasofia@gmail.com", savedUser.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    public void testFindByEmail(){
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setPassword("Password123");
        
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        
        Optional<User> foundUser = userRepository.findByEmail("test@mail.com");
        
        assertTrue(foundUser.isPresent());
        assertEquals(1L, foundUser.get().getId());
        verify(userRepository).findByEmail("test@mail.com");
    }

    @Test
    public void testFindByEmailNotFound() {
        when(userRepository.findByEmail("unknown@mail.com")).thenReturn(Optional.empty());
        
        Optional<User> foundUser = userRepository.findByEmail("unknown@mail.com");
        
        assertFalse(foundUser.isPresent());
        verify(userRepository).findByEmail("unknown@mail.com");
    }
}
