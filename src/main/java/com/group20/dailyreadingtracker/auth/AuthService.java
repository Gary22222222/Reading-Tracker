package com.group20.dailyreadingtracker.auth;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group20.dailyreadingtracker.role.Role;
import com.group20.dailyreadingtracker.role.RoleRepository;
import com.group20.dailyreadingtracker.user.User;
import com.group20.dailyreadingtracker.user.UserRepository;

// Handles user authentication and registration business logic

@Service
public class AuthService implements IAuthService{
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder encoder;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    @Transactional
    @Override
    public void register(User user){
        if (userRepository.existsByEmail(user.getEmail()) || 
            userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("User already exists");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        
        // Default role assignment
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
        .orElseGet(() -> {
            Role newRole = new Role("ROLE_USER");
            return roleRepository.save(newRole);
        });

        user.getRoles().clear();
        user.getRoles().add(userRole);

        userRepository.save(user);
    }
    
    @Override
    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAllUsers(){
        return userRepository.findAll();
    }

}
