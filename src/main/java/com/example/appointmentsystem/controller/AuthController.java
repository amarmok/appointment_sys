package com.example.appointmentsystem.controller;

import com.example.appointmentsystem.dto.AuthResponseTemp;
import com.example.appointmentsystem.dto.LoginRequest;
import com.example.appointmentsystem.dto.RegisterRequest;
import com.example.appointmentsystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public void register(@Valid @RequestBody RegisterRequest r) {
        authService.register(r);
    }

    @PostMapping("/login")
    public AuthResponseTemp login(@Valid @RequestBody LoginRequest r) {
        return authService.login(r);
    }
}
