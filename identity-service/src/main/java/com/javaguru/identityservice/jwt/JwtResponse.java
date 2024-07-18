package com.javaguru.identityservice.jwt;


//TODO:2
/** Класс-обертка для токена. Пока не используется */
public class JwtResponse {
    private String token;

    public JwtResponse(String token) {
        this.token = token;
    }

    // Геттер
    public String getToken() {
        return token;
    }
}
