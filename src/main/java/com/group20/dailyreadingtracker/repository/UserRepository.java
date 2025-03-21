package com.group20.dailyreadingtracker.repository;


import com.group20.dailyreadingtracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 根据 email 查找用户（用于登录和注册验证）
    Optional<User> findByEmail(String email);

    // 检查某个 email 是否已存在
    boolean existsByEmail(String email);
}

