package com.group20.dailyreadingtracker;

import java.util.List;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;

import com.group20.dailyreadingtracker.auth.AuthService;
import com.group20.dailyreadingtracker.role.Role;
import com.group20.dailyreadingtracker.role.RoleRepository;
import com.group20.dailyreadingtracker.user.User;
import com.group20.dailyreadingtracker.user.UserRepository;

import jakarta.transaction.Transactional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Use actual database
@Rollback(false)                                                             // Commit changes to db
public class AuthServiceTest {
    
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private AuthService authService;

    @BeforeEach
    public void setup(){
        userRepository.deleteAll();
        roleRepository.deleteAll();
        
        Role role = new Role();
        role.setName("ROLE_USER");
        entityManager.persist(role);
        entityManager.flush();
    }

    @Test
    public void testRegisterValidUser(){
        User user = new User();
        user.setEmail("test@mail.com");
        user.setPassword("Password123");

        authService.register(user);

        User savedUser = userRepository.findByEmail("test@mail.com").orElse(null);
        
        assertNotNull(savedUser, "User should be saved");
        assertEquals("test@mail.com", savedUser.getEmail(), "Email should match");
        assertTrue(encoder.matches("Password123", savedUser.getPassword()), "Password should be encoded");
        
        assertNotNull(savedUser.getRoles(), "User should have roles");
        assertEquals(1, savedUser.getRoles().size(), "User should have one role");
        assertTrue(savedUser.getRoles().stream()
            .anyMatch(r -> r.getName().equals("ROLE_USER")), "User should have ROLE_USER");
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
    @Transactional
    public void testFindAllUsers() {
        User user1 = new User();
        user1.setEmail("user1@mail.com");
        user1.setPassword("pass1");

        User user2 = new User();
        user2.setEmail("user2@mail.com");
        user2.setPassword("pass2");

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        List<User> users = authService.findAllUsers();
        assertEquals(2, users.size(), "Should find all users");
    }
}
