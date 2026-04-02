package com.challenge.swapi.dto;

public class AuthLoginResponseDTO {

    private final String token;
    private final String tokenType;

    public AuthLoginResponseDTO(String token, String tokenType) {
        this.token = token;
        this.tokenType = tokenType;
    }

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }
}
