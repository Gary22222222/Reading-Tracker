package com.group20.dailyreadingtracker.auth;

import java.time.LocalDateTime;

import com.group20.dailyreadingtracker.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public final class PasswordResetToken {

    private static final int EXPIRATION_TIME = 60;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @Column(unique=true)
    private String token;

    private LocalDateTime expirationTime;
    
    @OneToOne(targetEntity=User.class, fetch=FetchType.EAGER)
    @JoinColumn(nullable=false, name="user_id", unique=true)
    private User user;

    public PasswordResetToken(){}

    public PasswordResetToken(String token, User user){
        super();
        this.token = token;
        this.user = user;
        this.expirationTime = this.getTokenExpirationTime();
    }
    
    public PasswordResetToken(String token){
        super();
        this.token = token;
        this.expirationTime = this.getTokenExpirationTime();
    }
    
    public Long getId(){
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken(){
        return token;
    }

    public void setToken(String token){
        this.token = token;
    }

    public LocalDateTime getExpirationTime(){
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime){
        this.expirationTime = expirationTime;
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }

    public LocalDateTime getTokenExpirationTime() {
        return LocalDateTime.now().plusMinutes(EXPIRATION_TIME);
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expirationTime);
    }
}
