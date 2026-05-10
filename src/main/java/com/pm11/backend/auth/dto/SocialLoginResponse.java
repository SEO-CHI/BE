package com.pm11.backend.auth.dto;

public record SocialLoginResponse(UserInfo user, Tokens tokens) {}
