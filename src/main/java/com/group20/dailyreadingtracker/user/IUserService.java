package com.group20.dailyreadingtracker.user;

public interface IUserService {
    void save(User user);

    User findByUsername(String username);
}
