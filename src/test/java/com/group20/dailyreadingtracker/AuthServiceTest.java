package com.group20.dailyreadingtracker;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;

import com.group20.dailyreadingtracker.auth.AuthService;
import com.group20.dailyreadingtracker.role.Role;
import com.group20.dailyreadingtracker.user.User;
import com.group20.dailyreadingtracker.user.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Use actual database
@Rollback(false)                                                             // Commit changes to db
public class AuthServiceTest {
    
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private AuthService authService;

    @BeforeEach
    public void setup(){
        Role role = new Role();
        role.setName("ROLE_USER");
        entityManager.persist(role);
    }

    @Test
    public void testRegisterValidUser(){
        User user = new User();
        user.setEmail("test@mail.com");
        user.setPassword("Password123");

        authService.register(user);

        User savedUser = userRepository.findByEmail("test@mail.com").orElse(null);
        
        assertNotNull(savedUser);
        assertEquals("test@mail.com", savedUser.getEmail());
        assertTrue(encoder.matches("Password123", savedUser.getPassword()));
        
        assertNotNull(savedUser.getRoles());
        assertEquals(1, savedUser.getRoles().size());
        assertTrue(savedUser.getRoles().stream()
            .anyMatch(r -> r.getName().equals("ROLE_USER")));
    }

    @Test
    public void testFindByEmail(){
        User user = new User();
        user.setEmail("test@mail.com");
        user.setPassword("password");
        userRepository.save(user);

        Optional<User> foundUser = authService.findByEmail("test@mail.com");

        assertTrue(foundUser.isPresent());
        assertEquals("test@mail.com", foundUser.get().getEmail());
    }

    @Test
    public void testFindByEmailNotFound(){
        Optional<User> foundUser = authService.findByEmail("nonexistent@mail.com");
        assertFalse(foundUser.isPresent());
    }

    @Test
    public void testGeneratePasswordResetUrl(){
        User user = new User();
        user.setEmail("test@mail.com");
        userRepository.save(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName("localhost");
        request.setServerPort(8080);
        request.setContextPath("/");

        String token = "test-token";
        String url = authService.generatePasswordResetUrl(user, request, token);
    
        assertEquals("http://localhost:8080/app/reset-password?token=test-token", url);
    }


}
