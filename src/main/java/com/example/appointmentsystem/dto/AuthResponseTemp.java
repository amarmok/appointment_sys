package com.example.appointmentsystem.dto;

public class AuthResponseTemp {

    private String token;

    public AuthResponseTemp(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
