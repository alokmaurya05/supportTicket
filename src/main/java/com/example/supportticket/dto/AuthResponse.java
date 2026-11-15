package com.example.supportticket.dto;
public record AuthResponse(String accessToken, String tokenType, String username, String role) { }
