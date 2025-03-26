package com.group20.dailyreadingtracker.auth;

import java.util.List;

import com.group20.dailyreadingtracker.user.User;

public interface IAuthService {
    void register(User user);

    User findByEmail(String email);

    List<User> findAllUsers();
}
