package com.example.appointmentsystem.service;

import com.example.appointmentsystem.dto.*;
import com.example.appointmentsystem.entity.User;
import com.example.appointmentsystem.exception.BusinessException;
import com.example.appointmentsystem.repository.UserRepository;
import com.example.appointmentsystem.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already exists");
        }

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password())) 
                .role(request.role())
                .build();

        userRepository.save(user);
    }

    public AuthResponseTemp login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("User not found"));

        
        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponseTemp(token); 
    }
}
