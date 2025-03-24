package com.group20.dailyreadingtracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;

import com.group20.dailyreadingtracker.role.Role;
import com.group20.dailyreadingtracker.role.RoleRepository;
import com.group20.dailyreadingtracker.user.User;
import com.group20.dailyreadingtracker.user.UserRepository;
import com.group20.dailyreadingtracker.user.UserService;


@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE) // Use actual database
@Rollback(false)                                 // Commit changes to db
public class UserServiceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    public void testSaveUser(){
        UserService  userService = new UserService(userRepository, roleRepository, bCryptPasswordEncoder);

        User user = new User();
        user.setEmail("test@mail.com");
        user.setPassword("password");

        Role role = new Role();
        role.setName("ROLE_USER");
        entityManager.persist(role);

        userService.save(user);
        User savedUser = userRepository.findByEmail("test@mail.com");

        assertNotNull(savedUser);
        assertEquals("test@mail.com", savedUser);
        assertTrue(bCryptPasswordEncoder.matches("password", savedUser.getPassword()));

        assertNotNull(savedUser.getRoles());
        assertEquals(1, savedUser.getRoles().size());
        assertTrue(savedUser.getRoles().contains(role));
    }
    
}
