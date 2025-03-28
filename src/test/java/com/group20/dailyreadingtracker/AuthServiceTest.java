package com.group20.dailyreadingtracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @Test
    public void testRegisterUser(){
        AuthService authService = new AuthService(userRepository, roleRepository, encoder);

        Role role = new Role();
        role.setName("ROLE_USER");
        entityManager.persist(role);

        User user = new User();
        user.setEmail("test@mail.com");
        user.setPassword("password");

        authService.register(user);

        User savedUser = userRepository.findByEmail("test@mail.com");
        
        assertNotNull(savedUser);
        assertEquals("test@mail.com", savedUser.getEmail());
        assertTrue(encoder.matches("password", savedUser.getPassword()));
        
        assertNotNull(savedUser.getRoles());
        assertEquals(1, savedUser.getRoles().size());
        assertTrue(savedUser.getRoles().stream()
            .anyMatch(r -> r.getName().equals("ROLE_USER")));
    }

}
