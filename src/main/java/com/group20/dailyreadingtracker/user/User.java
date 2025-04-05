package com.group20.dailyreadingtracker.user;

import java.util.Set;

import com.group20.dailyreadingtracker.auth.VerificationToken;
import com.group20.dailyreadingtracker.role.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="username", nullable=false, length=40, unique=true)
    @Size(min=4, max=20, message="Username must be between 4-20 characters")
    private String username;

    @Column(nullable=false, unique=true)
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    @Column(nullable=false, length=64)
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, max = 64, message = "Password must be 8-64 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*$", message = "Password must contain at least 1 digit, 1 lowercase, and 1 uppercase letter")
    private String password;

    @Transient
    private String confirmPassword;
    
    @Column(nullable = false)
    private boolean isEnabled = false;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private VerificationToken verificationToken;

    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name="user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @Column(name ="avatar_file_name")
    private String avatarFileName;

    public User(){}

    public User(long id, String username, String email, String password,
            boolean isEnabled, VerificationToken verificationToken, Set<Role> roles, String avatarFileName) {
                super();
                this.id = id;
                this.username = username;
                this.email = email;
                this.password = password;
                this.isEnabled = isEnabled;
                this.verificationToken = verificationToken;
                this.roles = roles;
                this.avatarFileName = avatarFileName;
    }

    public long getId() {
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

    public String getConfirmPassword(){
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword){
        this.confirmPassword = confirmPassword;
    }

    public boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public VerificationToken getVerificationToken(){
        return verificationToken;
    }

    public void setVerificationToken(VerificationToken verificationToken){
        this.verificationToken = verificationToken;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getAvatarFilename(){
        return avatarFileName;
    }

    public void setAvatarFilename(String avatarFileName){
        this.avatarFileName = avatarFileName;
    }

    public boolean isPasswordsMatch() {
        return password != null && password.equals(confirmPassword);
    }
}
