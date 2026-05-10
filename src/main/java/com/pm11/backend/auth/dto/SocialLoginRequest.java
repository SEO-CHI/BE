package com.pm11.backend.auth.dto;

public record SocialLoginRequest(String provider, String id_token, String access_token) {}
