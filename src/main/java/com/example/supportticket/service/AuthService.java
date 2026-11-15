package com.example.supportticket.service;

import com.example.supportticket.dto.*;
import com.example.supportticket.entity.*;
import com.example.supportticket.exception.ConflictException;
import com.example.supportticket.repository.UserRepository;
import com.example.supportticket.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, AuthenticationManager authenticationManager, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.jwt = jwt;
    }

    public AuthResponse register(RegisterRequest request) {
        if (users.existsByUsername(request.username())) throw new ConflictException("Username is already in use");
        AppUser user = users.save(new AppUser(request.username(), encoder.encode(request.password()), Role.USER));
        log.info("Registered user username={}", user.getUsername());
        UserDetails details = org.springframework.security.core.userdetails.User.withUsername(user.getUsername()).password(user.getPassword()).authorities("ROLE_USER").build();
        return new AuthResponse(jwt.generateToken(details), "Bearer", user.getUsername(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        AppUser user = users.findByUsername(request.username()).orElseThrow();
        log.info("Successful login username={}", user.getUsername());
        UserDetails details = org.springframework.security.core.userdetails.User.withUsername(user.getUsername()).password(user.getPassword()).authorities("ROLE_" + user.getRole()).build();
        return new AuthResponse(jwt.generateToken(details), "Bearer", user.getUsername(), user.getRole().name());
    }
}
