package com.group20.dailyreadingtracker.auth;

import java.util.HashSet;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.group20.dailyreadingtracker.role.Role;
import com.group20.dailyreadingtracker.role.RoleRepository;
import com.group20.dailyreadingtracker.user.User;
import com.group20.dailyreadingtracker.user.UserRepository;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder encoder;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    public void register(User user){
        user.setPassword(encoder.encode(user.getPassword()));
        
        // Default role assignment
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null){
            userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
        }

        user.setRoles(new HashSet<>());
        user.getRoles().add(userRole);

        userRepository.save(user);
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public List<User> findAllUsers(){
        return userRepository.findAll();
    }


}
