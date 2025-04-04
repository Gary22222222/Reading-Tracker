package com.group20.dailyreadingtracker.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group20.dailyreadingtracker.user.User;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long>{
    
    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUser(User user);

    Optional<PasswordResetToken> findByUserEmail(String email);
}
