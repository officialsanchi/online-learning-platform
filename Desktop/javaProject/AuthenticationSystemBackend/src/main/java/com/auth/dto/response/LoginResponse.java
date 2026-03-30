package com.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class LoginResponse {
    public LoginResponse(String accessToken, String refreshToken, String tokenType, String expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private String expiresIn;
}
