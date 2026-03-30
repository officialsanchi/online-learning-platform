package com.auth.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TwoFactorRequest {
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private String code;
}
