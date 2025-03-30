package com.group20.dailyreadingtracker.auth;

import java.util.Calendar;
import java.util.Date;

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

    private static final int EXPIRATION_TIME = 1440;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @Column(unique=true)
    private String token;

    private Date expirationTime;
    
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

    public Date getExpirationTime(){
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime){
        this.expirationTime = expirationTime;
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }

    public Date getTokenExpirationTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, EXPIRATION_TIME);
        return new Date(calendar.getTime().getTime());
    }

    public boolean isExpired(){
        return new Date().after(this.expirationTime);
    }
}
