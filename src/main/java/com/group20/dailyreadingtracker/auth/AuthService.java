package com.group20.dailyreadingtracker.auth;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.group20.dailyreadingtracker.role.Role;
import com.group20.dailyreadingtracker.role.RoleRepository;
import com.group20.dailyreadingtracker.user.User;
import com.group20.dailyreadingtracker.user.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthService implements IAuthService{
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder encoder;
    private final PasswordResetTokenService passwordResetTokenService;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder encoder, PasswordResetTokenService passwordResetTokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.passwordResetTokenService = passwordResetTokenService;
}

    @Override
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

    @Override
    public void createPasswordResetTokenForUser(User user, String passwordToken){
        passwordResetTokenService.createPasswordResetTokenForUser(user, passwordToken);
    }

    @Override
    public String generatePasswordResetUrl(User user, HttpServletRequest request, String token) {
        String baseUrl = request.getRequestURL().toString()
                .replace(request.getServletPath(), "");
        return baseUrl + "/reset-password?token=" + token;
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
