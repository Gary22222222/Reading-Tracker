package com.group20.dailyreadingtracker;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.group20.dailyreadingtracker.user.User;
import com.group20.dailyreadingtracker.user.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE) // Use actual database
@Rollback(false)                                 // Commit changes to db
public class UserRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreateUser(){
        User user = new User();
        user.setEmail("mamonovasofia@gmail.com");
        user.setPassword("Iamnottellingyou8");
        user.setUsername("slooonya");

        User savedUser = userRepository.save(user);
        User existUser = entityManager.find(User.class, savedUser.getId());

        assertEquals(existUser.getEmail(), user.getEmail());
    }

    @Test
    public void testFindByEmail(){
        User savedUser = new User();
        savedUser.setEmail("test@mail.com");
        savedUser.setPassword("Password123");

        entityManager.persist(savedUser);

        Optional<User> foundUser = userRepository.findByEmail("test@mail.com");
        
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
    }
}
