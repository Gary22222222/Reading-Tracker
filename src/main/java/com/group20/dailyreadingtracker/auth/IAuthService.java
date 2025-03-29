package com.group20.dailyreadingtracker.auth;

import java.util.List;
import java.util.Optional;

import com.group20.dailyreadingtracker.user.User;

import jakarta.servlet.http.HttpServletRequest;

public interface IAuthService {
    void register(User user);

    Optional<User> findByEmail(String email);

    List<User> findAllUsers();

    void createPasswordResetTokenForUser(User user, String passwordToken);

    String generatePasswordResetUrl(User user, HttpServletRequest request, String token);
}
