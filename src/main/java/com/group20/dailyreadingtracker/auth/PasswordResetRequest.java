package com.group20.dailyreadingtracker.auth;

import java.util.Objects;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PasswordResetRequest {
    private String token;
    
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, max = 64, message = "Password must be 8-64 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*$", 
             message = "Password must contain at least 1 digit, 1 lowercase and 1 uppercase letter")
    private String newPassword;

    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;

    public PasswordResetRequest(){}

    public PasswordResetRequest(String token, String email, String newPassword, String confirmPassword){
        this.token = token;
        this.email = email;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getToken(){
        return token;
    }

    public void setToken(String token){
        this.token = token;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getNewPassword(){
        return newPassword;
    }

    public void setNewPawword(String newPassword){
        this.newPassword = newPassword;
    }

    public String getConfirmPassword(){
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword){
        this.confirmPassword = confirmPassword;
    }

    @Override
    public String toString(){
        return "PasswordResetRequest{" + "email='" + email + '\'' + 
        ", newPassword='" + newPassword + '\'' +
        ", confirmPassword='" + confirmPassword + '\'' + '}';
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordResetRequest that = (PasswordResetRequest) o;
        return Objects.equals(email, that.email) &&
               Objects.equals(newPassword, that.newPassword) &&
               Objects.equals(confirmPassword, that.confirmPassword);
    }

    @Override
    public int hashCode(){
        return Objects.hash(email, newPassword, confirmPassword);
    }
}
