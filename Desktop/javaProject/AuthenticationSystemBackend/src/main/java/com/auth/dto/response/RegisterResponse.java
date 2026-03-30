package com.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class RegisterResponse {
    public RegisterResponse(String userId, String email, String message, String status) {
        this.userId = userId;
        this.email = email;
        this.message = message;
        this.status = status;
    }

    private String userId;
    private String email;
    private String message;
    private String status;
}
