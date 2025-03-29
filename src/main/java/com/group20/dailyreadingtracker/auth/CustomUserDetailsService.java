package com.group20.dailyreadingtracker.auth;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.group20.dailyreadingtracker.role.Role;
import com.group20.dailyreadingtracker.user.User;
import com.group20.dailyreadingtracker.user.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (!optionalUser.isPresent())
            throw new UsernameNotFoundException("Invalid email or password");

        User user = optionalUser.get();

        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : user.getRoles())
            authorities.add(new SimpleGrantedAuthority(role.getName()));

        return new org.springframework.security.core.userdetails.User(
            user.getEmail(), user.getPassword(), authorities);
    }
    
}

