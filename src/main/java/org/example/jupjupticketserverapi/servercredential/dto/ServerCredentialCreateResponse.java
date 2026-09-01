package org.example.jupjupticketserverapi.servercredential.dto;

import lombok.Getter;

@Getter
public class ServerCredentialCreateResponse {

    private final String apiKey;

    public ServerCredentialCreateResponse(String apiKey) {
        this.apiKey = apiKey;
    }
}
