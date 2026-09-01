package org.example.jupjupticketserverapi.auth.dto;

import lombok.Getter;

@Getter
public class AuthVerifyResponse {

    private final Long userId;
    private final String email;
    private final String name;

    public AuthVerifyResponse(Long userId, String email, String name) {
        this.userId = userId;
        this.email = email;
        this.name = name;
    }
}
