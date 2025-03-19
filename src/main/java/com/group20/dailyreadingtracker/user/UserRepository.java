package com.group20.dailyreadingtracker.user;

import com.group20.dailyreadingtracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
