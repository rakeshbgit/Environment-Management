package com.apimanagement.Environment.Auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

public class AuthRequest {
    @NotNull @Email @Length(min = 5, max = 50)
    private String username;

    @NotNull @Length(min = 5, max = 10)
    private String password;

    public AuthRequest() {

    }

    public AuthRequest(String username,String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
