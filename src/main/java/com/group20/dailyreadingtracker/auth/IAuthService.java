package com.group20.dailyreadingtracker.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.group20.dailyreadingtracker.user.User;

import jakarta.servlet.http.HttpServletRequest;

public interface IAuthService {
    void register(User user, MultipartFile avatar, HttpServletRequest request);

    Optional<User> findByEmail(String email);

    List<User> findAllUsers();
}
