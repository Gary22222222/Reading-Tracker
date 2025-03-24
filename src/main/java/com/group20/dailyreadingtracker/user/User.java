package com.group20.dailyreadingtracker.user;

import java.util.Set;

import com.group20.dailyreadingtracker.role.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="username", nullable=false, length=40)
    private String username;

    @Column(nullable=false, unique=true, length=50)
    private String email;

    @Column(nullable=false, length=64)
    private String password;
    private boolean isEnabled = false; // Email verification

    @ManyToMany
    private Set<Role> roles;

    public User(){}

    public User(long id, String username, String email, String password,
            boolean isEnabled, Set<Role> roles) {
                super();
                this.id = id;
                this.username = username;
                this.email = email;
                this.password = password;
                this.isEnabled = isEnabled;
                this.roles = roles;
    }
    private boolean isLocked = false;  // 用户是否被锁定

    // Getters & Setters
    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

}
