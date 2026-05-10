package com.pm11.backend.auth.dto;

public record Tokens(String access_token, String refresh_token, String token_type, long expires_in) {}
