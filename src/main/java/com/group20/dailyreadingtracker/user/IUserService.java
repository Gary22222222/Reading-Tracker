package com.group20.dailyreadingtracker.user;

import com.group20.dailyreadingtracker.entity.User;

public interface IUserService {
    void save(User user);

    User findByUsername(String username);
}
