package com.wiftwift.controller;

import com.wiftwift.dto.JwtAuthenticationResponse;
import com.wiftwift.dto.RegisterRequest;
import com.wiftwift.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public JwtAuthenticationResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request.getName(), request.getPassword());
    }

    @PostMapping("/login")
    public JwtAuthenticationResponse login(@RequestBody RegisterRequest request) {
        return authService.login(request.getName(), request.getPassword());
    }
}

