package com.group20.dailyreadingtracker.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group20.dailyreadingtracker.user.User;


public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long>{
    Optional<VerificationToken> findByUserEmail(String email);
    Optional<VerificationToken> findByToken(String token);

    void deleteByUserEmail(String email);

    void deleteByUser(User user);
}
