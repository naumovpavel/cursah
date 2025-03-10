package com.wiftwift.service;

import com.wiftwift.authentication.service.JwtService;
import com.wiftwift.dto.JwtAuthenticationResponse;
import com.wiftwift.exception.ForbiddenException;
import com.wiftwift.model.User;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

    public JwtAuthenticationResponse register(String name, String password) {
        var user = User.builder().username(name).password(passwordEncoder.encode(password)).build();
        user = userService.create(user);
        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(user.getId(), user.getUsername(), jwt, user.getRole());
    }

    public JwtAuthenticationResponse login(String name, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(name, password));
        } catch (Exception e) {
            throw new ForbiddenException("неправильное имя пользователя или пароль.");
        }
        var userDetails = userService.getByUsername(name);
        var jwt = jwtService.generateToken(userDetails);
        return new JwtAuthenticationResponse(userDetails.getId(), userDetails.getUsername(), jwt, userDetails.getRole());
    }
}
