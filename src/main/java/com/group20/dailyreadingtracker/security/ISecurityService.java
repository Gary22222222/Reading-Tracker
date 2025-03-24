package com.group20.dailyreadingtracker.security;

public interface ISecurityService {
    
    boolean isAuthenticated();

    void autoLogin(String username, String password);
}
