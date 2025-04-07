package com.group20.dailyreadingtracker.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.group20.dailyreadingtracker.user.User;


public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long>{
    Optional<VerificationToken> findByUserEmail(String email);
    Optional<VerificationToken> findByToken(String token);

    @Modifying
    @Transactional
    void deleteByUserEmail(String email);

    @Modifying
    @Query("DELETE FROM VerificationToken vt WHERE vt.user = :user")
    void deleteAllByUser(@Param("user") User user);
}
