package com.auth.dto.request;

import lombok.Getter;
import lombok.Setter;


public class LoginRequest {
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String identifier;
    private String password;

}
